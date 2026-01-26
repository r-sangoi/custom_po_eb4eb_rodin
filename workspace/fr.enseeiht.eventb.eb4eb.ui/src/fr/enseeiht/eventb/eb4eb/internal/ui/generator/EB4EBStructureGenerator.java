package fr.enseeiht.eventb.eb4eb.internal.ui.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDestructorExtension;
import org.eventb.core.ast.datatype.ITypeConstructorExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.maths.extensions.WorkspaceExtensionsManager;
import org.rodinp.core.RodinDBException;

import fr.enseeiht.eventb.eb4eb.internal.ui.EB4EBAstUtils;
import fr.enseeiht.eventb.eb4eb.ui.EB4EBUIPlugin;

public class EB4EBStructureGenerator {

	private static final String CHECK_OPERATOR_NAME = "check_Machine_Consistency"; //TODO : extensibilité
	
	private static final String EVENTS_SET_NAME = "Ev";
	private static final String MACHINE_NAME = "mch";
	private static final String EVENT_VARIABLE_NAME = "e";
	private static final String VARIANT_VARIABLE_NAME = "v";

	private IContextRoot root;
	private FormulaFactory factory;

	private ITypeConstructorExtension machineTypeExtension;
	private Map<String, IDestructorExtension> destructors;
	private IPredicateExtension checkOperator;

	public EB4EBStructureGenerator(IContextRoot root) throws CoreException {
		this.root = root;
		this.factory = FormulaFactory.getDefault();
		this.destructors = new HashMap<String, IDestructorExtension>();

		ITheoryPathRoot theoryPath = DatabaseUtilitiesTheoryPath.getTheoryPath(EB4EBUIPlugin.THEORY_PATH_NAME,
				root.getRodinFile().getRodinProject());

		WorkspaceExtensionsManager mgr = WorkspaceExtensionsManager.getInstance();
		
		IDeployedTheoryRoot coreTheory = null;
		for (IAvailableTheory avTheory : theoryPath.getAvailableTheories()) {
			if(avTheory.getAvailableTheoryProject().getElementName().equals(EB4EBUIPlugin.THEORY_CORE_PROJECT_NAME) &&
					avTheory.getLabel().equals(EB4EBUIPlugin.THEORY_CORE_FILE_NAME))
				coreTheory = avTheory.getDeployedTheory();
		}
		if(coreTheory==null)
			throw newCoreException("TODO : Trouver un message d'erreur"); //TODO
		
		Set<IFormulaExtension> extensions = mgr.getFormulaExtensions(coreTheory);
		this.factory = this.factory.withExtensions(extensions);
		for (IFormulaExtension extension : extensions) {
			if (extension instanceof ITypeConstructorExtension machineTypeExtension) {
				if (this.machineTypeExtension != null)
					throw newCoreException("TODO : Trouver un message d'erreur"); //TODO
				this.machineTypeExtension = machineTypeExtension;
			} else if (extension instanceof IDestructorExtension destructor) {
				this.destructors.put(destructor.getSyntaxSymbol(), destructor);
			} else if (extension instanceof IPredicateExtension operatorExtension && operatorExtension.getSyntaxSymbol().equals(CHECK_OPERATOR_NAME)) {
				if (this.checkOperator != null)
					throw newCoreException("TODO : Trouver un message d'erreur"); //TODO
				this.checkOperator = operatorExtension;
			}
		}
	}

	public void generateEB4EBStructure(ISCMachineRoot mch, IProgressMonitor pMonitor) throws CoreException {
		if(mch.getSCVariants().length>1)
			throw newCoreException("TODO : Trouver un message d'erreur"); //TODO
		
		FormulaFactory savedFactory = this.factory;

		try {
			this.factory = this.factory.withExtensions(mch.getTypeEnvironment().getFormulaFactory().getExtensions());

			// Generate Carrier Set
			generateEB4EBEventCarrierSet(pMonitor);

			// Generate Constants
			generateEB4EBEventsConstant(mch.getSCEvents(), pMonitor);
			generateMachineConstant(pMonitor);

			// Generate Axioms
			Counter axiomLabelCounter = new Counter("axm%d");
			generateAxiomPartitionEvents(axiomLabelCounter, mch.getSCEvents(), pMonitor);
			generateAxiomMachineType(axiomLabelCounter, mch.getSCVariables(), pMonitor);
			generateAxiomEventsProgress(axiomLabelCounter, mch.getSCEvents(), pMonitor);
			generateAxiomEventsConvergence(axiomLabelCounter, mch.getSCEvents(), pMonitor);
			generateAxiomInvariant(axiomLabelCounter, mch.getSCVariables(), mch.getSCInvariants(), pMonitor);
			generateAxiomTheorem(axiomLabelCounter, mch.getSCVariables(), mch.getSCInvariants(), pMonitor);
			int variantLength = mch.getSCVariants().length;
			generateAxiomVariant(axiomLabelCounter, mch.getSCVariables(),
					variantLength == 1 ? mch.getSCVariants()[0] : null, pMonitor);
			generateAxiomAP(axiomLabelCounter, mch.getSCVariables(), mch.getSCEvents(), pMonitor);
			generateAxiomGuard(axiomLabelCounter, mch.getSCVariables(), mch.getSCEvents(), pMonitor);
			generateAxiomBAP(axiomLabelCounter, mch.getSCVariables(), mch.getSCEvents(), pMonitor);

			// Generate Theorem
			generateTheoremCheck(axiomLabelCounter, pMonitor);
			
		} finally {
			// Roll back the factory
			this.factory = savedFactory;
		}
	}

	private void generateEB4EBEventCarrierSet(IProgressMonitor pMonitor) throws RodinDBException {
		ICarrierSet evSet = this.root.createChild(ICarrierSet.ELEMENT_TYPE, null, pMonitor);
		evSet.setIdentifierString(EVENTS_SET_NAME, pMonitor);
	}

	private void generateEB4EBEventsConstant(ISCEvent[] events, IProgressMonitor pMonitor) throws RodinDBException {
		for (ISCEvent ev : events) {
			IConstant evConstant = this.root.createChild(IConstant.ELEMENT_TYPE, null, pMonitor);
			evConstant.setIdentifierString(ev.getLabel(), pMonitor);
		}
	}

	private void generateMachineConstant(IProgressMonitor pMonitor) throws RodinDBException {
		IConstant mchConstant = this.root.createChild(IConstant.ELEMENT_TYPE, null, pMonitor);
		mchConstant.setIdentifierString(MACHINE_NAME, pMonitor);
	}

	private void generateAxiomPartitionEvents(Counter axiomLabelCounter, ISCEvent[] events, IProgressMonitor pMonitor)
			throws RodinDBException {
		IAxiom axiomPartition = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomPartition.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomPartition.setTheorem(false, pMonitor);
		List<Expression> exps = new ArrayList<Expression>();
		exps.add(this.factory.makeFreeIdentifier(EVENTS_SET_NAME, null));
		for (ISCEvent ev : events) {
			exps.add(this.factory.makeSetExtension(this.factory.makeFreeIdentifier(ev.getLabel(), null), null));
		}
		axiomPartition.setPredicateString(this.factory.makeMultiplePredicate(Formula.KPARTITION, exps, null).toString(),
				pMonitor);
	}

	private void generateAxiomMachineType(Counter axiomLabelCounter, ISCVariable[] vars, IProgressMonitor pMonitor)
			throws CoreException {
		IAxiom axiomMchType = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomMchType.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomMchType.setTheorem(false, pMonitor);
		
		IAxiom axiomStateType = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomStateType.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomStateType.setTheorem(false, pMonitor);
		
		IAxiom axiomEventType = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomEventType.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomEventType.setTheorem(false, pMonitor);

		List<Expression> typeExpressions = new ArrayList<Expression>();

		Type[] types = new Type[vars.length];
		for (int i = 0; i < vars.length; i++) {
			types[i] = vars[i].getType(this.factory);
		}
		typeExpressions.add(EB4EBAstUtils.makeProductType(this.factory, types));
		typeExpressions.add(this.factory.makeFreeIdentifier(EVENTS_SET_NAME, null));

		Expression machineType = this.factory.makeExtendedExpression(this.machineTypeExtension, typeExpressions,
				new ArrayList<Predicate>(), null);
		axiomMchType.setPredicateString(this.factory.makeRelationalPredicate(Formula.IN,
				this.factory.makeFreeIdentifier(MACHINE_NAME, null), machineType, null).toString(), pMonitor);
		
		Expression stateDestructor = this.factory.makeExtendedExpression(this.destructors.get("State"), Collections.singleton(this.factory.makeFreeIdentifier(MACHINE_NAME, null)),
				new ArrayList<Predicate>(), null);
		axiomStateType.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL,
				stateDestructor, typeExpressions.get(0), null).toString(), pMonitor);
		
		Expression eventDestructor = this.factory.makeExtendedExpression(this.destructors.get("Event"), Collections.singleton(this.factory.makeFreeIdentifier(MACHINE_NAME, null)),
				new ArrayList<Predicate>(), null);
		axiomEventType.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL,
				eventDestructor, typeExpressions.get(1), null).toString(), pMonitor);
	}

	private void generateAxiomEventsProgress(Counter axiomLabelCounter, ISCEvent[] events,
			IProgressMonitor pMonitor) throws CoreException {
		IAxiom axiomInit = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomInit.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomInit.setTheorem(false, pMonitor);
		
		IAxiom axiomProgress = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomProgress.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomProgress.setTheorem(false, pMonitor);
		
		// Generate destructors
		Expression initDestructor = this.factory.makeExtendedExpression(this.destructors.get("Init"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		Expression progressDestructor = this.factory.makeExtendedExpression(this.destructors.get("Progress"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		
		Expression initEvent = null;
		List<Expression> progressEvents = new ArrayList<Expression>();
		for (ISCEvent event : events) {
			if(isInitialisation(event)) {
				if(initEvent != null)
					throw newCoreException("TODO : Trouver un message d'erreur"); //TODO
				initEvent = this.factory.makeFreeIdentifier(event.getLabel(), null);
			} else {
				progressEvents.add(this.factory.makeFreeIdentifier(event.getLabel(), null));
			}
		}
		
		axiomInit.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, initDestructor, initEvent, null).toString(), pMonitor);
		axiomProgress.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, progressDestructor, this.factory.makeSetExtension(progressEvents, null), null).toString(), pMonitor);
	}

	private void generateAxiomEventsConvergence(Counter axiomLabelCounter, ISCEvent[] events,
			IProgressMonitor pMonitor) throws CoreException {
		IAxiom axiomOrdinary = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomOrdinary.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomOrdinary.setTheorem(false, pMonitor);

		IAxiom axiomConvergent = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomConvergent.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomConvergent.setTheorem(false, pMonitor);

//		IAxiom axiomAnticipated = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
//		axiomAnticipated.setLabel(axiomLabelCounter.next(), pMonitor);
//		axiomAnticipated.setTheorem(false, pMonitor);
		
		// Generate destructors
		Expression ordinaryDestructor = this.factory.makeExtendedExpression(this.destructors.get("Ordinary"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		Expression convergentDestructor = this.factory.makeExtendedExpression(this.destructors.get("Convergent"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
//		Expression anticipatedDestructor = this.factory.makeExtendedExpression(this.destructors.get("Anticipated"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);

		
		List<Expression> ordinaryEvents = new ArrayList<Expression>();
		List<Expression> convergentEvents = new ArrayList<Expression>();
		List<Expression> anticipatedEvents = new ArrayList<Expression>();
		for (ISCEvent event : events) {
			switch (event.getConvergence()) {
			case ORDINARY:
				ordinaryEvents.add(this.factory.makeFreeIdentifier(event.getLabel(), null));
				break;
			case CONVERGENT:
				convergentEvents.add(this.factory.makeFreeIdentifier(event.getLabel(), null));
				break;
			case ANTICIPATED:
				anticipatedEvents.add(this.factory.makeFreeIdentifier(event.getLabel(), null));
				break;
			}
		}
		
		axiomOrdinary.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, ordinaryDestructor, this.factory.makeSetExtension(ordinaryEvents, null), null).toString(), pMonitor);
		axiomConvergent.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, convergentDestructor, this.factory.makeSetExtension(convergentEvents, null), null).toString(), pMonitor);
//		axiomAnticipated.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, anticipatedDestructor, this.factory.makeSetExtension(anticipatedEvents, null), null).toString(), pMonitor);
	}
	
	private void generateAxiomInvariant(Counter axiomLabelCounter, ISCVariable[] vars, ISCInvariant[] invs, IProgressMonitor pMonitor) throws CoreException {
		IAxiom axiomInvariant = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomInvariant.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomInvariant.setTheorem(false, pMonitor);
		
		Expression invariantDestructor = this.factory.makeExtendedExpression(this.destructors.get("Inv"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		
		Map<FreeIdentifier, Expression> bindingMap = new HashMap<FreeIdentifier, Expression>();
		BoundIdentDecl[] bids = new BoundIdentDecl[vars.length];
		FreeIdentifier[] maplets = new FreeIdentifier[vars.length];
		
		for (int i = 0; i < vars.length; i++) {
			FreeIdentifier fid = vars[i].getIdentifier(this.factory);
			bids[i] = fid.asDecl();
			maplets[i] = fid;
			bindingMap.put(fid, this.factory.makeBoundIdentifier(vars.length - i - 1, fid.getSourceLocation(), fid.getType()));
		}
		
		List<Predicate> preds = new ArrayList<Predicate>();
		for (ISCInvariant inv : invs) {
			if(!inv.isTheorem()) {
				Predicate pred = inv.getPredicate(((ISCMachineRoot)inv.getRoot()).getTypeEnvironment());
				pred = pred.translate(this.factory);
				if(!EB4EBAstUtils.isATypePred(pred))
					preds.add(pred);
			}
		}
		Predicate setPredicate = this.factory.makeAssociativePredicate(Formula.LAND, preds, null);
		
		Expression setExpression = EB4EBAstUtils.makeMapletVars(factory, maplets);
		
		// Link the identifiers
		setPredicate = setPredicate.substituteFreeIdents(bindingMap);
		setExpression = setExpression.substituteFreeIdents(bindingMap);
		
		Expression invariantExpression = this.factory.makeQuantifiedExpression(Formula.CSET, bids, setPredicate, setExpression, null, Form.Explicit);
		axiomInvariant.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, invariantDestructor, invariantExpression, null).toString(), pMonitor);
	}
	
	private void generateAxiomTheorem(Counter axiomLabelCounter, ISCVariable[] vars, ISCInvariant[] invs, IProgressMonitor pMonitor) throws CoreException {
		IAxiom axiomTheorem = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomTheorem.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomTheorem.setTheorem(false, pMonitor);
		
		Expression theoremDestructor = this.factory.makeExtendedExpression(this.destructors.get("Thm"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		
		if(invs.length == 0 || vars.length == 0) {
			Expression theoremExpression = this.factory.makeEmptySet(null, null);
			axiomTheorem.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, theoremDestructor, theoremExpression, null).toString(), pMonitor);
			return;
		}
		
		Map<FreeIdentifier, Expression> bindingMap = new HashMap<FreeIdentifier, Expression>();
		BoundIdentDecl[] bids = new BoundIdentDecl[vars.length];
		FreeIdentifier[] maplets = new FreeIdentifier[vars.length];
		
		for (int i = 0; i < vars.length; i++) {
			FreeIdentifier fid = vars[i].getIdentifier(this.factory);
			bids[i] = fid.asDecl();
			maplets[i] = fid;
			bindingMap.put(fid, this.factory.makeBoundIdentifier(vars.length - i - 1, fid.getSourceLocation(), fid.getType()));
		}
		
		List<Predicate> preds = new ArrayList<Predicate>();
		for (ISCInvariant inv : invs) {
			if(inv.isTheorem()) {
				Predicate pred = inv.getPredicate(((ISCMachineRoot)inv.getRoot()).getTypeEnvironment());
				pred = pred.translate(this.factory);
				preds.add(pred);
			}
		}
		Predicate setPredicate;
		if (preds.size() == 1) {
			setPredicate = preds.get(0);
		} else {
			setPredicate = this.factory.makeAssociativePredicate(Formula.LAND, preds, null);			
		}
		
		Expression setExpression = EB4EBAstUtils.makeMapletVars(factory, maplets);
		
		// Link the identifiers
		setPredicate = setPredicate.substituteFreeIdents(bindingMap);
		setExpression = setExpression.substituteFreeIdents(bindingMap);
		
		Expression theoremExpression = this.factory.makeQuantifiedExpression(Formula.CSET, bids, setPredicate, setExpression, null, Form.Explicit);
		axiomTheorem.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, theoremDestructor, theoremExpression, null).toString(), pMonitor);
	}
	
	private void generateAxiomVariant(Counter axiomLabelCounter, ISCVariable[] vars, ISCVariant variant, IProgressMonitor pMonitor) throws CoreException {
		IAxiom axiomVariant = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomVariant.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomVariant.setTheorem(false, pMonitor);
		
		Expression variantDestructor = this.factory.makeExtendedExpression(this.destructors.get("Variant"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		
		if(variant == null) {
			Expression variantExpression = this.factory.makeEmptySet(null, null);
			axiomVariant.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, variantDestructor, variantExpression, null).toString(), pMonitor);
			return;
		}
		
		Map<FreeIdentifier, Expression> bindingMap = new HashMap<FreeIdentifier, Expression>();
		BoundIdentDecl[] bids = new BoundIdentDecl[vars.length + 1];
		FreeIdentifier[] maplets = new FreeIdentifier[vars.length];
		
		bids[vars.length] = this.factory.makeBoundIdentDecl(VARIANT_VARIABLE_NAME, null);
		bindingMap.put(this.factory.makeFreeIdentifier(VARIANT_VARIABLE_NAME, null), this.factory.makeBoundIdentifier(0, null));
		for (int i = 0; i < vars.length; i++) {
			FreeIdentifier fid = vars[i].getIdentifier(this.factory);
			bids[i] = fid.asDecl();
			maplets[i] = fid;
			bindingMap.put(fid, this.factory.makeBoundIdentifier(vars.length - i, fid.getSourceLocation(), fid.getType()));
		}
		
		Predicate setPredicate = this.factory.makeRelationalPredicate(Formula.EQUAL,
				this.factory.makeFreeIdentifier(VARIANT_VARIABLE_NAME, null), 
				variant.getExpression(((ISCMachineRoot)variant.getRoot()).getTypeEnvironment()).translate(this.factory), null);
		
		Expression mapletsExpression = EB4EBAstUtils.makeMapletVars(this.factory, maplets);
		Expression setExpression = this.factory.makeBinaryExpression(Formula.MAPSTO, mapletsExpression, this.factory.makeFreeIdentifier(VARIANT_VARIABLE_NAME, null), null);

		// Link the identifiers
		setPredicate = setPredicate.substituteFreeIdents(bindingMap);
		setExpression = setExpression.substituteFreeIdents(bindingMap);
		
		Expression variantExpression = this.factory.makeQuantifiedExpression(Formula.CSET, bids, setPredicate, setExpression, null, Form.Explicit);
		axiomVariant.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, variantDestructor, variantExpression, null).toString(), pMonitor);
	}
	
	private void generateAxiomAP(Counter axiomLabelCounter, ISCVariable[] vars, ISCEvent[] events, IProgressMonitor pMonitor) throws CoreException {
		IAxiom axiomAP = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomAP.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomAP.setTheorem(false, pMonitor);
		
		Expression apDestructor = this.factory.makeExtendedExpression(this.destructors.get("AP"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		
		Map<FreeIdentifier, Expression> bindingMap = new HashMap<FreeIdentifier, Expression>();
		BoundIdentDecl[] bids = new BoundIdentDecl[vars.length];
		FreeIdentifier[] maplets = new FreeIdentifier[vars.length];
		
		for (int i = 0; i < vars.length; i++) {
			FreeIdentifier fid = vars[i].getIdentifier(this.factory);
			FreeIdentifier fidp = this.factory.makeFreeIdentifier(fid.getName() + "p", fid.getSourceLocation(), fid.getType());
			bids[i] = fidp.asDecl();
			maplets[i] = fidp;
			bindingMap.put(fidp, this.factory.makeBoundIdentifier(vars.length - i - 1, fidp.getSourceLocation(), fidp.getType()));
		}
		Predicate setPredicate = null;
		
		for (ISCEvent event : events) {
			if(isInitialisation(event)) {
				setPredicate = getActionsComprehension(this.factory, event);
				break; //TODO : erreur ???
			}
		}
		
		Expression setExpression = EB4EBAstUtils.makeMapletVars(factory, maplets);
		
		// Link the identifiers
		setPredicate = setPredicate.substituteFreeIdents(bindingMap);
		setExpression = setExpression.substituteFreeIdents(bindingMap);
		
		Expression apExpression = this.factory.makeQuantifiedExpression(Formula.CSET, bids, setPredicate, setExpression, null, Form.Explicit);
		axiomAP.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, apDestructor, apExpression, null).toString(), pMonitor);
	}
	
	private void generateAxiomGuard(Counter axiomLabelCounter, ISCVariable[] vars, ISCEvent[] events, IProgressMonitor pMonitor)
			throws CoreException {
		IAxiom axiomGuard = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomGuard.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomGuard.setTheorem(false, pMonitor);
		
		// Generate destructor
		Expression guardDestructor = this.factory.makeExtendedExpression(this.destructors.get("Grd"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		
		if(events.length == 0 || vars.length == 0) {
			Expression apExpression = this.factory.makeEmptySet(null, null);
			axiomGuard.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, guardDestructor, apExpression, null).toString(), pMonitor);
			return;
		}

		// Map to link the parameters
		Map<FreeIdentifier, Expression> bindingMap = new HashMap<FreeIdentifier, Expression>();
		BoundIdentDecl[] bids = new BoundIdentDecl[vars.length + 1];
		FreeIdentifier[] maplets = new FreeIdentifier[vars.length];
		
		bids[0] = this.factory.makeBoundIdentDecl(EVENT_VARIABLE_NAME, null);
		bindingMap.put(this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null), this.factory.makeBoundIdentifier(vars.length, null));
		for (int i = 0; i < vars.length; i++) {
			FreeIdentifier fid = vars[i].getIdentifier(this.factory);
			
			// Normal BoundIdentifier
			bids[i + 1] = fid.asDecl();
			
			// Normal Maplet
			maplets[i] = fid;
			
			bindingMap.put(fid, this.factory.makeBoundIdentifier(vars.length - i - 1, fid.getSourceLocation(), fid.getType()));
		}

		// Generate the predicate of the comprehension set
		List<Predicate> eventsPredicate = new ArrayList<Predicate>();
		for (ISCEvent event : events) {
			if(isInitialisation(event))
				continue;
			ISCGuard[] guards = event.getSCGuards();
			Predicate[] guardsPredicates = new Predicate[guards.length + 1];
			guardsPredicates[0] = this.factory.makeRelationalPredicate(Formula.EQUAL,
					this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null),
					this.factory.makeFreeIdentifier(event.getLabel(), null), null);
			for (int i = 0; i < guards.length; i++) {
				Predicate pred = guards[i].getPredicate(((ISCMachineRoot)event.getRoot()).getTypeEnvironment());
				pred = pred.translate(this.factory);
				guardsPredicates[i + 1] = pred;
			}
			Predicate eventPredicate;
			if (guardsPredicates.length == 0) {
				eventPredicate = this.factory.makeLiteralPredicate(Formula.BTRUE, null);
			} else if (guardsPredicates.length == 1) {
				eventPredicate = guardsPredicates[0];
			} else {
				eventPredicate = this.factory.makeAssociativePredicate(Formula.LAND, guardsPredicates, null);			
			}
			eventsPredicate.add(eventPredicate);
		}
		
		Predicate setPredicate;
		if (eventsPredicate.size() == 1) {
			setPredicate = eventsPredicate.get(0);
		} else {
			setPredicate = this.factory.makeAssociativePredicate(Formula.LOR, eventsPredicate, null);			
		}

		// Generate the expression of the comprehension set
		Expression mapletsExpression = EB4EBAstUtils.makeMapletVars(factory, maplets);
		Expression setExpression = this.factory.makeBinaryExpression(Formula.MAPSTO, this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null), mapletsExpression, null);

		// Link the identifiers
		setPredicate = setPredicate.substituteFreeIdents(bindingMap);
		setExpression = setExpression.substituteFreeIdents(bindingMap);
		
		Expression guardExpression = this.factory.makeQuantifiedExpression(Formula.CSET, bids, setPredicate, setExpression, null, Form.Explicit);
		axiomGuard.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, guardDestructor, guardExpression, null).toString(), pMonitor);
	}
	
	private void generateAxiomBAP(Counter axiomLabelCounter, ISCVariable[] vars, ISCEvent[] events, IProgressMonitor pMonitor)
			throws CoreException {
		IAxiom axiomBAP = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		axiomBAP.setLabel(axiomLabelCounter.next(), pMonitor);
		axiomBAP.setTheorem(false, pMonitor);
		
		// Generate destructor
		List<Expression> destructorParams = Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null));
		Expression destructor = this.factory.makeExtendedExpression(this.destructors.get("BAP"), destructorParams, new ArrayList<Predicate>(), null);
		
		if(events.length == 0 || vars.length == 0) {
			Expression bapExpression = this.factory.makeEmptySet(null, null);
			axiomBAP.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, destructor, bapExpression, null).toString(), pMonitor);
			return;
		}

		// Map to link the parameters
		Map<FreeIdentifier, Expression> substituteMap = new HashMap<FreeIdentifier, Expression>();
		BoundIdentDecl[] bids = new BoundIdentDecl[2*vars.length + 1];
		FreeIdentifier[] maplets = new FreeIdentifier[vars.length];
		FreeIdentifier[] primedMaplets = new FreeIdentifier[vars.length];
		
		bids[0] = this.factory.makeBoundIdentDecl(EVENT_VARIABLE_NAME, null);
		substituteMap.put(this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null), this.factory.makeBoundIdentifier(2 * vars.length, null));
		for (int i = 0; i < vars.length; i++) {
			FreeIdentifier fid = vars[i].getIdentifier(this.factory);
			FreeIdentifier fidp = this.factory.makeFreeIdentifier(fid.getName() + "p", fid.getSourceLocation(), fid.getType());
			
			// Normal BoundIdentifier
			bids[i + 1] = fid.asDecl();
			// Primed BoundIdentifier
			bids[vars.length + i + 1] = fidp.asDecl();
			
			// Normal Maplet
			maplets[i] = fid;
			// Primed Maplet
			primedMaplets[i] = fidp;
			
			substituteMap.put(fid, this.factory.makeBoundIdentifier(2*vars.length - i - 1, fid.getSourceLocation(), fid.getType()));
			substituteMap.put(fidp, this.factory.makeBoundIdentifier(vars.length - i - 1, fidp.getSourceLocation(), fidp.getType()));
		}

		// Generate the predicate of the comprehension set
		List<Predicate> eventsPredicate = new ArrayList<Predicate>();
		for (ISCEvent event : events) {
			if(isInitialisation(event))
				continue;
			Predicate[] eventPredicates = new Predicate[] {
					this.factory.makeRelationalPredicate(Formula.EQUAL,
							this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null),
							this.factory.makeFreeIdentifier(event.getLabel(), null), null),
					getActionsComprehension(factory, event)
			};
			eventsPredicate.add(this.factory.makeAssociativePredicate(Formula.LAND, eventPredicates, null));
		}
		
		Predicate setPredicate;
		if (eventsPredicate.size() == 1) {
			setPredicate = eventsPredicate.get(0);
		} else {
			setPredicate = this.factory.makeAssociativePredicate(Formula.LOR, eventsPredicate, null);			
		}

		// Generate the expression of the comprehension set
		Expression mapletsExpression = EB4EBAstUtils.makeMapletVars(factory, maplets);
		Expression primedMapletsExpression = EB4EBAstUtils.makeMapletVars(factory, primedMaplets);
		Expression setExpression = this.factory.makeBinaryExpression(Formula.MAPSTO, mapletsExpression, primedMapletsExpression, null);
		setExpression = this.factory.makeBinaryExpression(Formula.MAPSTO, this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null), setExpression, null);

		// Link the identifiers
		setPredicate = setPredicate.substituteFreeIdents(substituteMap);
		setExpression = setExpression.substituteFreeIdents(substituteMap);
		
//		TODO : flatten
		
		
		Expression bapExpression = this.factory.makeQuantifiedExpression(Formula.CSET, bids, setPredicate, setExpression, null, Form.Explicit);
		axiomBAP.setPredicateString(this.factory.makeRelationalPredicate(Formula.EQUAL, destructor, bapExpression, null).toString(), pMonitor);
	}

	private void generateTheoremCheck(Counter axiomLabelCounter, IProgressMonitor pMonitor) throws RodinDBException {
		IAxiom theoremCheck = this.root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
		theoremCheck.setLabel(axiomLabelCounter.next(), pMonitor);
		theoremCheck.setTheorem(true, pMonitor);
		
		Collection<Expression> checkArgs = Collections.singleton(this.factory.makeFreeIdentifier(MACHINE_NAME, null));
		theoremCheck.setPredicateString(this.factory.makeExtendedPredicate(this.checkOperator, checkArgs, new ArrayList<Predicate>(), null).toString(), pMonitor);
	}

	private static CoreException newCoreException(String message) {
		IStatus status = new Status(IStatus.ERROR, EB4EBUIPlugin.PLUGIN_ID, message);
		return new CoreException(status);
	}
	
	private static boolean isInitialisation(ISCEvent scEv) throws RodinDBException {
		IEvent[] nonSCEvents = ((IMachineRoot) EventBPlugin.asMachineFile(scEv.getRodinFile()).getRoot()).getChildrenOfType(IEvent.ELEMENT_TYPE);
		IEvent ev = null;
		int i = 0;
		while(ev==null) {
			if(nonSCEvents[i].getLabel().equals(scEv.getLabel()))
				ev = nonSCEvents[i];
			i++;
		}
		return ev.isInitialisation();
	}

	private static Predicate getActionsComprehension(FormulaFactory factory, ISCEvent event) throws CoreException {
		ISCMachineRoot mch = (ISCMachineRoot) event.getRoot();

		List<FreeIdentifier> remainingFids = new ArrayList<FreeIdentifier>();
		for (ISCVariable var : mch.getSCVariables()) {
			FreeIdentifier fid = var.getIdentifier(factory);
			remainingFids.add(fid);
		}

		List<Assignment> assignments = new ArrayList<Assignment>();
		for (ISCAction action : event.getSCActions()) {
			Assignment assignment = action.getAssignment(mch.getTypeEnvironment());
			assignments.add(assignment);
			remainingFids.removeAll(Arrays.asList(assignment.getAssignedIdentifiers()));
		}

		for (FreeIdentifier fid : remainingFids) {
			assignments.add(factory.makeBecomesEqualTo(fid, fid, null));
		}

		Predicate pred = EB4EBAstUtils.makeCombinedBAs(factory, assignments.toArray(new Assignment[0]));

		IFormulaRewriter primeRewriter = new DefaultRewriter(false) {
			@Override
			public Expression rewrite(FreeIdentifier identifier) {
				if (!identifier.isPrimed())
					return super.rewrite(identifier);
				return factory.makeFreeIdentifier(identifier.withoutPrime().getName() + "p",
						identifier.getSourceLocation(), identifier.getType());
			}
		};

		return pred.rewrite(primeRewriter);
	}
	
	private class Counter {
		private String string;
		private int i;
		
		public Counter(String string){
			this.string = string;
		}
		
		public String next() {
			return string.formatted(++i);
		}
	}
}