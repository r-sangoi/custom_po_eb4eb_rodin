package fr.enseeiht.eventb.eb4eb.internal.ui.builder;

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
import org.eventb.core.IContextRoot;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.datatype.IDestructorExtension;
import org.eventb.core.ast.datatype.ITypeConstructorExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.maths.extensions.WorkspaceExtensionsManager;

import fr.enseeiht.eventb.eb4eb.internal.ui.EB4EBAstUtils;
import fr.enseeiht.eventb.eb4eb.internal.ui.callbacks.ContextCallback;
import fr.enseeiht.eventb.eb4eb.ui.EB4EBUIPlugin;

public class EB4EBContextBuilder extends ContextBuilder {

	private static final String CHECK_OPERATOR_NAME = "check_Machine_Consistency"; // TODO : extensibilité

	private static final String EVENTS_SET_NAME = "Ev";
	private static final String MACHINE_NAME = "mch";
	private static final String VARIANT_VARIABLE_NAME = "v";
	private static final String EVENT_VARIABLE_NAME = "e";

	private static final String STATE_SET_NAME = "St";

	private FormulaFactory factory;
	private HashMap<String, IDestructorExtension> destructors;
	private ITypeConstructorExtension machineTypeExtension;
	private IPredicateExtension checkOperator;

	private List<FreeIdentifier> fids;
	private List<FreeIdentifier> fidps;
	private List<BoundIdentDecl> bids;
	private List<BoundIdentDecl> bidps;
	private List<Type> variableTypes;
	private List<Predicate> invsPred;
	private List<Predicate> thmsPred;
	private Expression variant;
	private List<String> events;
	private List<Expression> progressEvents;
	private Expression initEvent;
	private List<Expression> ordinaryEvents;
	private List<Expression> convergentEvents;
	private List<Expression> anticipatedEvents; // TODO
	private Predicate AP;
	private Map<String, Predicate> eventsGuard;
	private Map<String, Predicate> eventsBAP;
	private Map<String, List<FreeIdentifier>> eventsUnassignedFids;

	private IFormulaRewriter primeRewriter;

	public EB4EBContextBuilder(IContextRoot root, IProgressMonitor pMonitor) throws CoreException {
		super(root, pMonitor);
		this.factory = FormulaFactory.getDefault();
		this.destructors = new HashMap<String, IDestructorExtension>();

		ITheoryPathRoot theoryPath = DatabaseUtilitiesTheoryPath.getTheoryPath(EB4EBUIPlugin.THEORY_PATH_NAME, root.getRodinFile().getRodinProject());

		WorkspaceExtensionsManager mgr = WorkspaceExtensionsManager.getInstance();

		IDeployedTheoryRoot coreTheory = null;
		for (IAvailableTheory avTheory : theoryPath.getAvailableTheories()) {
			if (avTheory.getAvailableTheoryProject().getElementName().equals(EB4EBUIPlugin.THEORY_CORE_PROJECT_NAME)
					&& avTheory.getLabel().equals(EB4EBUIPlugin.THEORY_CORE_FILE_NAME))
				coreTheory = avTheory.getDeployedTheory();
		}
		if (coreTheory == null)
			throw newCoreException("TODO : Trouver un message d'erreur"); // TODO

		Set<IFormulaExtension> extensions = mgr.getFormulaExtensions(coreTheory);
		this.factory = this.factory.withExtensions(extensions);
		for (IFormulaExtension extension : extensions) {
			if (extension instanceof ITypeConstructorExtension machineTypeExtension) {
				if (this.machineTypeExtension != null)
					throw newCoreException("TODO : Trouver un message d'erreur"); // TODO
				this.machineTypeExtension = machineTypeExtension;
			} else if (extension instanceof IDestructorExtension destructor) {
				this.destructors.put(destructor.getSyntaxSymbol(), destructor);
			} else if (extension instanceof IPredicateExtension operatorExtension && operatorExtension.getSyntaxSymbol().equals(CHECK_OPERATOR_NAME)) {
				if (this.checkOperator != null)
					throw newCoreException("TODO : Trouver un message d'erreur"); // TODO
				this.checkOperator = operatorExtension;
			}
		}

		this.fids = new ArrayList<FreeIdentifier>();
		this.fidps = new ArrayList<FreeIdentifier>();
		this.bids = new ArrayList<BoundIdentDecl>();
		this.bidps = new ArrayList<BoundIdentDecl>();
		this.variableTypes = new ArrayList<Type>();
		this.invsPred = new ArrayList<Predicate>();
		this.thmsPred = new ArrayList<Predicate>();
		this.variant = null;
		this.events = new ArrayList<String>();
		this.progressEvents = new ArrayList<Expression>();
		this.initEvent = null;
		this.ordinaryEvents = new ArrayList<Expression>();
		this.convergentEvents = new ArrayList<Expression>();
		this.anticipatedEvents = new ArrayList<Expression>();
		this.AP = null;
		this.eventsGuard = new HashMap<String, Predicate>();
		this.eventsBAP = new HashMap<String, Predicate>();
		this.eventsUnassignedFids = new HashMap<String, List<FreeIdentifier>>();

		this.primeRewriter = new DefaultRewriter(false) {
			@Override
			public Expression rewrite(FreeIdentifier identifier) {
				if (!identifier.isPrimed())
					return super.rewrite(identifier);
				return factory.makeFreeIdentifier(identifier.withoutPrime().getName() + "p", identifier.getSourceLocation(), identifier.getType());
			}
		};
	}

	/** 
	 * Update the formula factory.
	 * <p>Must be called if custom theory plugin is used in the machine.</p>

	 * @param machineFactory the factory of the machine
	 */
	public void translate(FormulaFactory machineFactory) {
		this.factory = this.factory.withExtensions(machineFactory.getExtensions());
	}

	public void addVariableIdentifier(FreeIdentifier fidBase) {
		FreeIdentifier fid = (FreeIdentifier) fidBase.translate(this.factory);
		FreeIdentifier fidp = this.factory.makeFreeIdentifier(fid.getName() + "p", fid.getSourceLocation(), fid.getType());

		fids.add(fid);
		fidps.add(fidp);
		bids.add(fid.asDecl());
		bidps.add(fidp.asDecl());
	}

	/**
	 * Add type of one variable.
	 * @param type the type of the variable
	 */
	public void addVariableType(Type type) {
		this.variableTypes.add(type.translate(this.factory));
	}

	public void addInvariantPredicate(Predicate pred) {
		if (!EB4EBAstUtils.isATypePred(pred))
			this.invsPred.add(pred.translate(this.factory));
	}

	public void addTheoremPredicate(Predicate pred) {
		this.thmsPred.add(pred.translate(this.factory));
	}

	public void setVariant(Expression variant) throws CoreException {
		if (this.variant != null)
			throw newCoreException("TODO : Trouver un message d'erreur"); // TODO
		this.variant = variant.translate(this.factory);
	}

	public void addEventName(String name) {
		events.add(name);
	}

	public void addProgressEvent(String name) {
		this.progressEvents.add(this.factory.makeFreeIdentifier(name, null));
		this.eventsBAP.put(name, this.factory.makeRelationalPredicate(Formula.EQUAL, this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null),
				this.factory.makeFreeIdentifier(name, null), null));
		this.eventsGuard.put(name, this.factory.makeRelationalPredicate(Formula.EQUAL, this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null),
				this.factory.makeFreeIdentifier(name, null), null));
		this.eventsUnassignedFids.put(name, new ArrayList<FreeIdentifier>(this.fids));
	}

	public void setInitEvent(String name) throws CoreException {
		if (this.initEvent != null)
			throw newCoreException("TODO : Trouver un message d'erreur"); // TODO
		this.initEvent = this.factory.makeFreeIdentifier(name, null);
	}

	public void addOrdinaryEvent(String name) {
		this.ordinaryEvents.add(this.factory.makeFreeIdentifier(name, null));
	}

	public void addConvergentEvent(String name) {
		this.convergentEvents.add(this.factory.makeFreeIdentifier(name, null));
	}

	public void addAnticipatedEvent(String name) {
		this.anticipatedEvents.add(this.factory.makeFreeIdentifier(name, null));
	}

	public void addInitAction(Assignment assignment) {
		Predicate pred = assignment.getBAPredicate().translate(this.factory).rewrite(this.primeRewriter);
		if (this.AP == null) {
			this.AP = pred;
		} else {
			List<Predicate> preds = new ArrayList<Predicate>();
			preds.add(this.AP);
			preds.add(pred);
			this.AP = this.factory.makeAssociativePredicate(Formula.LAND, preds, null).flatten();
		}
	}

	public void addEventGuard(String eventName, Predicate guard) {
		if (!this.eventsGuard.containsKey(eventName)) {
			throw new RuntimeException("addProgressEvent need to be called before");
		} else {
			List<Predicate> preds = new ArrayList<Predicate>();
			preds.add(this.eventsGuard.get(eventName));
			preds.add(guard);
			this.eventsGuard.put(eventName, this.factory.makeAssociativePredicate(Formula.LAND, preds, null).flatten());
		}
	}

	public void addEventBAP(String eventName, Assignment assignment) {
		this.eventsUnassignedFids.get(eventName).removeAll(Arrays.asList(assignment.getAssignedIdentifiers()));
		Predicate pred = assignment.getBAPredicate().translate(this.factory).rewrite(this.primeRewriter);
		if (!this.eventsBAP.containsKey(eventName)) {
			throw new RuntimeException("addProgressEvent need to be called before");
		} else {
			List<Predicate> preds = new ArrayList<Predicate>();
			preds.add(this.eventsBAP.get(eventName));
			preds.add(pred);
			this.eventsBAP.put(eventName, this.factory.makeAssociativePredicate(Formula.LAND, preds, null).flatten());
		}
	}

	public void fillUnassignedBAP(String eventName) {
		for (FreeIdentifier fid : this.eventsUnassignedFids.get(eventName)) {
			List<Predicate> preds = new ArrayList<Predicate>();
			preds.add(this.eventsBAP.get(eventName));
			preds.add(this.factory.makeBecomesEqualTo(fid, fid, null).getBAPredicate().rewrite(this.primeRewriter));
			this.eventsBAP.put(eventName, this.factory.makeAssociativePredicate(Formula.LAND, preds, null).flatten());
		}
	}

	@Override
	public void build() throws CoreException {
		// Carrier Set
		super.addCarrierSet(EVENTS_SET_NAME);
		if(this.variableTypes.size() == 0) {
			super.addCarrierSet(STATE_SET_NAME);
		}

		// Constants
		for (String ev : this.events) {
			super.addConstant(ev);
		}
		super.addConstant(MACHINE_NAME);
		
		// Axioms
		generateAxiomPartitionEvents();
		generateAxiomMachineType();
		generateAxiomEventsProgress();
		generateAxiomEventsConvergence();
		generateAxiomInvariant();
		generateAxiomTheorem();
		generateAxiomVariant();
		generateAxiomAP();
		generateAxiomGuard();
		generateAxiomBAP();
		
		// Theorem
		Collection<Expression> checkArgs = Collections.singleton(this.factory.makeFreeIdentifier(MACHINE_NAME, null));
		super.addAxiom(this.factory.makeExtendedPredicate(this.checkOperator, checkArgs, new ArrayList<Predicate>(), null), true);
		
		for (ContextCallback callback : super.getCallbacks()) {
			Predicate pred = callback.call(this.factory, MACHINE_NAME, this.events);
			super.addAxiom(pred, true);
		}
	}

	private void generateAxiomPartitionEvents() throws CoreException {
		List<Expression> exps = new ArrayList<Expression>();
		exps.add(this.factory.makeFreeIdentifier(EVENTS_SET_NAME, null));
		for (String ev : this.events) {
			exps.add(this.factory.makeSetExtension(this.factory.makeFreeIdentifier(ev, null), null));
		}
		if(this.events.size() == 0) {
			super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, exps.get(0), this.factory.makeEmptySet(null, null), null), false);
		} else {
			super.addAxiom(this.factory.makeMultiplePredicate(Formula.KPARTITION, exps, null), false);
		}
		if(this.variableTypes.size() == 0) {
			super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, this.factory.makeFreeIdentifier(STATE_SET_NAME, null), this.factory.makeEmptySet(null, null), null), false);
		}
	}

	private void generateAxiomMachineType() throws CoreException {
		List<Expression> typeExpressions = new ArrayList<Expression>();

		if(this.variableTypes.size() == 0) {
			typeExpressions.add(this.factory.makeFreeIdentifier(STATE_SET_NAME, null));
		} else {
			typeExpressions.add(EB4EBAstUtils.makeProductType(this.factory, this.variableTypes));
		}
		typeExpressions.add(this.factory.makeFreeIdentifier(EVENTS_SET_NAME, null));

		Expression machineType = this.factory.makeExtendedExpression(this.machineTypeExtension, typeExpressions, new ArrayList<Predicate>(), null);
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.IN, this.factory.makeFreeIdentifier(MACHINE_NAME, null), machineType, null), false);

		Expression stateDestructor = this.factory.makeExtendedExpression(this.destructors.get("State"),
				Collections.singleton(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, stateDestructor, typeExpressions.get(0), null), false);

		Expression eventDestructor = this.factory.makeExtendedExpression(this.destructors.get("Event"),
				Collections.singleton(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, eventDestructor, typeExpressions.get(1), null), false);
	}

	private void generateAxiomEventsProgress() throws CoreException {
		Expression initDestructor = this.factory.makeExtendedExpression(this.destructors.get("Init"),
				Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		Expression progressDestructor = this.factory.makeExtendedExpression(this.destructors.get("Progress"),
				Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		if(this.initEvent!=null)
			super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, initDestructor, initEvent, null), false);
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, progressDestructor, this.factory.makeSetExtension(progressEvents, null), null),
				false);
	}

	private void generateAxiomEventsConvergence() throws CoreException {
		Expression ordinaryDestructor = this.factory.makeExtendedExpression(this.destructors.get("Ordinary"),
				Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		Expression convergentDestructor = this.factory.makeExtendedExpression(this.destructors.get("Convergent"),
				Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
//		Expression anticipatedDestructor = this.factory.makeExtendedExpression(this.destructors.get("Anticipated"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, ordinaryDestructor, this.factory.makeSetExtension(ordinaryEvents, null), null),
				false);
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, convergentDestructor, this.factory.makeSetExtension(convergentEvents, null), null),
				false);
//		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, anticipatedDestructor, this.factory.makeSetExtension(anticipatedEvents, null), null), false);
	}

	private void generateAxiomInvariant() throws CoreException {
		Expression invariantDestructor = this.factory.makeExtendedExpression(this.destructors.get("Inv"),
				Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);

		Predicate setPredicate;
		if(this.invsPred.size() == 0) {
			setPredicate = this.factory.makeLiteralPredicate(Formula.BTRUE, null);
		} else if (this.invsPred.size() == 1) {
			setPredicate = this.invsPred.get(0);
		} else {
			setPredicate = this.factory.makeAssociativePredicate(Formula.LAND, this.invsPred, null);
		}

		Expression setMaplets = EB4EBAstUtils.makeMapletVars(this.factory, this.fids);
		
		Expression invariantExpression;
		if(setMaplets == null) {
			invariantExpression = this.factory.makeEmptySet(null, null);
		} else {
			setPredicate = setPredicate.bindTheseIdents(this.fids);
			setMaplets = setMaplets.bindTheseIdents(this.fids);

			invariantExpression = this.factory.makeQuantifiedExpression(Formula.CSET, this.bids, setPredicate, setMaplets, null, Form.Explicit);
		}
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, invariantDestructor, invariantExpression, null), false);

	}

	private void generateAxiomTheorem() throws CoreException {
		Expression theoremDestructor = this.factory.makeExtendedExpression(this.destructors.get("Thm"),
				Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);

		Predicate setPredicate;
		if(this.thmsPred.size() == 0) {
			setPredicate = this.factory.makeLiteralPredicate(Formula.BTRUE, null);
		} else if (this.thmsPred.size() == 1) {
			setPredicate = this.thmsPred.get(0);
		} else {
			setPredicate = this.factory.makeAssociativePredicate(Formula.LAND, this.thmsPred, null);
		}

		Expression setMaplets = EB4EBAstUtils.makeMapletVars(this.factory, this.fids);
		
		Expression theoremExpression;
		if(setMaplets == null) {
			theoremExpression = this.factory.makeEmptySet(null, null);
		} else {
			setPredicate = setPredicate.bindTheseIdents(this.fids);
			setMaplets = setMaplets.bindTheseIdents(this.fids);

			theoremExpression = this.factory.makeQuantifiedExpression(Formula.CSET, this.bids, setPredicate, setMaplets, null, Form.Explicit);
		}
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, theoremDestructor, theoremExpression, null), false);

	}

	private void generateAxiomVariant() throws CoreException {
		Expression variantDestructor = this.factory.makeExtendedExpression(this.destructors.get("Variant"),
				Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);

		List<FreeIdentifier> variantFids = new ArrayList<FreeIdentifier>(this.fids);
		variantFids.add(this.factory.makeFreeIdentifier(VARIANT_VARIABLE_NAME, null));
		List<BoundIdentDecl> variantBids = new ArrayList<BoundIdentDecl>(this.bids);
		variantBids.add(this.factory.makeBoundIdentDecl(VARIANT_VARIABLE_NAME, null));

		Expression variantExpression;
		if(this.variant == null) {
			variantExpression = this.factory.makeEmptySet(null, null);
		} else {			
			Predicate setPredicate = this.factory.makeRelationalPredicate(Formula.EQUAL, this.factory.makeFreeIdentifier(VARIANT_VARIABLE_NAME, null), this.variant,
					null);
			setPredicate = setPredicate.bindTheseIdents(variantFids);
	
			Expression setMaplets = EB4EBAstUtils.makeMapletVars(this.factory, variantFids);
			setMaplets = setMaplets.bindTheseIdents(variantFids);
	
			variantExpression = this.factory.makeQuantifiedExpression(Formula.CSET, variantBids, setPredicate, setMaplets, null, Form.Explicit);	
		}
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, variantDestructor, variantExpression, null), false);
	}

	private void generateAxiomAP() throws CoreException {
		Expression apDestructor = this.factory.makeExtendedExpression(this.destructors.get("AP"),
				Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);

		Expression apExpression;
		if(this.AP == null) {
			apExpression = this.factory.makeEmptySet(null, null);
		} else {
			this.AP = this.AP.bindTheseIdents(this.fidps);
	
			Expression setMaplets = EB4EBAstUtils.makeMapletVars(this.factory, this.fidps);
			setMaplets = setMaplets.bindTheseIdents(this.fidps);
			
			apExpression = this.factory.makeQuantifiedExpression(Formula.CSET, bidps, this.AP, setMaplets, null, Form.Explicit);
		}
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, apDestructor, apExpression, null), false);
	}

	private void generateAxiomGuard() throws CoreException {
		Expression guardDestructor = this.factory.makeExtendedExpression(this.destructors.get("Grd"),
				Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);

		List<FreeIdentifier> guardFids = new ArrayList<FreeIdentifier>();
		guardFids.add(this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null));
		guardFids.addAll(this.fids);
		List<BoundIdentDecl> guardBids = new ArrayList<BoundIdentDecl>();
		guardBids.add(this.factory.makeBoundIdentDecl(EVENT_VARIABLE_NAME, null));
		guardBids.addAll(this.bids);

		Predicate setPredicate;
		if(this.eventsGuard.size() == 0) {
			setPredicate = this.factory.makeLiteralPredicate(Formula.BTRUE, null);
		} else if (this.eventsGuard.size() == 1) {
			setPredicate = this.eventsGuard.values().iterator().next();
		} else {
			setPredicate = this.factory.makeAssociativePredicate(Formula.LOR, this.eventsGuard.values(), null);
		}
		setPredicate = setPredicate.bindTheseIdents(guardFids);

		Expression setMaplets = EB4EBAstUtils.makeMapletVars(this.factory, this.fids);
		if(setMaplets == null) {
			setMaplets = this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null);
		} else {
			setMaplets = this.factory.makeBinaryExpression(Formula.MAPSTO, this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null), setMaplets, null);
		}
		setMaplets = setMaplets.bindTheseIdents(guardFids);

		Expression guardExpression = this.factory.makeQuantifiedExpression(Formula.CSET, guardBids, setPredicate, setMaplets, null, Form.Explicit);
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, guardDestructor, guardExpression, null), false);
	}

	private void generateAxiomBAP() throws CoreException {
		Expression bapDestructor = this.factory.makeExtendedExpression(this.destructors.get("BAP"), Collections.singletonList(this.factory.makeFreeIdentifier(MACHINE_NAME, null)), new ArrayList<Predicate>(), null);
				
		List<FreeIdentifier> bapFids = new ArrayList<FreeIdentifier>();
		bapFids.add(this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null));
		bapFids.addAll(this.fids);
		bapFids.addAll(this.fidps);
		List<BoundIdentDecl> bapBids = new ArrayList<BoundIdentDecl>();
		bapBids.add(this.factory.makeBoundIdentDecl(EVENT_VARIABLE_NAME, null));
		bapBids.addAll(this.bids);
		bapBids.addAll(this.bidps);
		
		Predicate setPredicate;
		if(this.eventsBAP.size() == 0) {
			setPredicate = this.factory.makeLiteralPredicate(Formula.BTRUE, null);
		} else if (this.eventsBAP.size() == 1) {
			setPredicate = this.eventsBAP.values().iterator().next();
		} else {
			setPredicate = this.factory.makeAssociativePredicate(Formula.LOR, this.eventsBAP.values(), null);
		}
		setPredicate = setPredicate.bindTheseIdents(bapFids);
		
		Expression mapletsExpression = EB4EBAstUtils.makeMapletVars(factory, this.fids);
		Expression primedMapletsExpression = EB4EBAstUtils.makeMapletVars(factory, this.fidps);
		Expression setMaplets;
		if(mapletsExpression == null || primedMapletsExpression == null) {
			setMaplets = this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null);
		} else {
			setMaplets = this.factory.makeBinaryExpression(Formula.MAPSTO, mapletsExpression, primedMapletsExpression, null);
			setMaplets = this.factory.makeBinaryExpression(Formula.MAPSTO, this.factory.makeFreeIdentifier(EVENT_VARIABLE_NAME, null), setMaplets, null);
					}
		setMaplets = setMaplets.bindTheseIdents(bapFids);
		
		Expression bapExpression = this.factory.makeQuantifiedExpression(Formula.CSET, bapBids, setPredicate, setMaplets, null, Form.Explicit);
		super.addAxiom(this.factory.makeRelationalPredicate(Formula.EQUAL, bapDestructor, bapExpression, null), false);
	}

	private static CoreException newCoreException(String message) {
		IStatus status = new Status(IStatus.ERROR, EB4EBUIPlugin.PLUGIN_ID, message);
		return new CoreException(status);
	}
}
