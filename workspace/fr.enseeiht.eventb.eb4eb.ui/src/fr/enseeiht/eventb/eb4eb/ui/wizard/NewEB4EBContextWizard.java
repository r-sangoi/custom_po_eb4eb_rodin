package fr.enseeiht.eventb.eb4eb.ui.wizard;

import static org.eclipse.ui.wizards.newresource.BasicNewResourceWizard.selectAndReveal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IContextRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.ITheoryPathRoot;

import fr.enseeiht.eventb.eb4eb.internal.ui.EB4EBUIUtils;
import fr.enseeiht.eventb.eb4eb.internal.ui.acceptor.MachineRootAcceptor;
import fr.enseeiht.eventb.eb4eb.internal.ui.builder.EB4EBContextBuilder;
import fr.enseeiht.eventb.eb4eb.internal.ui.generator.TheoryPathGenerator;
import fr.enseeiht.eventb.eb4eb.internal.ui.visitor.EB4EBStructurGeneratorVisitor;
import fr.enseeiht.eventb.eb4eb.ui.EB4EBUIPlugin;

public class NewEB4EBContextWizard extends Wizard implements INewWizard {

	
	/**
	 * The identifier of the new EB4EB context wizard (value
	 * <code>"fr.enseeiht.eb4eb.generator.ui.wizards.NewEB4EBContext"</code>).
	 */
	public static final String WIZARD_ID = EB4EBUIPlugin.PLUGIN_ID
			+ ".wizards.NewEB4EBContext";

	// The wizard page.
	private NewEB4EBContextWizardPage page;

	// The selection when the wizard is launched.
//	private ISelection selection;
	
	// The statically checked selected machine when the wizard is launched.
	private ISCMachineRoot selection;

	// The workbench when the wizard is launched
	private IWorkbench workbench;

	/**
	 * Constructor: This wizard needs a progress monitor.
	 */
	public NewEB4EBContextWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	@Override
	public void addPages() {
		page = new NewEB4EBContextWizardPage(selection);
		addPage(page);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;

		if (!(selection instanceof IStructuredSelection))
			return;
		
		IRodinFile rodinFile = EventBPlugin.asSCMachineFile(selection.getFirstElement());
		this.selection  = (ISCMachineRoot) rodinFile.getRoot();
	}

	@Override
	public boolean performFinish() {
		
		final String projectName = page.getProjectName();
		final String contextName = page.getContextName() + ".buc";
		final ISCMachineRoot mch = selection;
		
		IRunnableWithProgress operation = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					doFinish(projectName, contextName, mch, monitor);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
		};
		
		try {
			getContainer().run(true, false, operation);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException
					.getMessage());
			return false;
		}
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject newProject = root.getProject(projectName);
		selectAndReveal(newProject, workbench.getActiveWorkbenchWindow());
		
		return true;
	}
	
	void doFinish(String projectName, String contextName, ISCMachineRoot mch,
			IProgressMonitor monitor) throws RodinDBException {
		monitor.beginTask("Creating EB4EB model of " + mch.getElementName(), 3);
		
		final IRodinProject rodinProject = EventBUIPlugin.getRodinDatabase().getRodinProject(projectName);
		
		RodinCore.run(new IWorkspaceRunnable() {
			
			@Override
			public void run(IProgressMonitor pMonitor) throws CoreException {
				IProject project = rodinProject.getProject();
				if (!project.exists())
					project.create(null);
				project.open(null);
				IProjectDescription description = project.getDescription();
				description.setNatureIds(new String[] { RodinCore.NATURE_ID });
				project.setDescription(description, null);
			}
		}, monitor);
		
		monitor.worked(1);
		
		RodinCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor pMonitor) throws CoreException {
				final IRodinFile rodinFile = rodinProject.getRodinFile(DatabaseUtilitiesTheoryPath.getTheoryPathFullName(EB4EBUIPlugin.THEORY_PATH_NAME));
				rodinFile.create(false, pMonitor);
				final IInternalElement rodinRoot = rodinFile.getRoot();
				((IConfigurationElement) rodinRoot).setConfiguration(DatabaseUtilitiesTheoryPath.THEORY_PATH_CONFIGURATION, pMonitor);
				
				//Generate TheoryPath
				new TheoryPathGenerator((ITheoryPathRoot) rodinRoot).generateTheoryPath(mch, pMonitor);
				
				rodinFile.save(null, true);
			}

		}, monitor);
		
		monitor.worked(1);
		
		final IRodinFile rodinFile = rodinProject
				.getRodinFile(contextName);
		
		RodinCore.run(new IWorkspaceRunnable() {
			
			@Override
			public void run(IProgressMonitor pMonitor) throws CoreException {
				rodinFile.create(false, pMonitor);
				final IInternalElement rodinRoot = rodinFile.getRoot();
				((IConfigurationElement) rodinRoot).setConfiguration(
						IConfigurationElement.DEFAULT_CONFIGURATION, pMonitor);
				
				// Generate EB4EB Structure
//				new EB4EBStructureGenerator((IContextRoot) rodinRoot)
//						.generateEB4EBStructure(mch, pMonitor);
				EB4EBContextBuilder b = new EB4EBContextBuilder((IContextRoot) rodinRoot, pMonitor);
				new MachineRootAcceptor<EB4EBContextBuilder>(mch).accept(new EB4EBStructurGeneratorVisitor(), b);
				b.build();
				rodinFile.save(null, true);
			}
		}, monitor);
		
		monitor.worked(1);
		
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				EB4EBUIUtils.linkToEventBEditor(rodinFile);
			}
		});
		monitor.worked(1);
	}
	
//	void generateEB4EBStructure(IContextRoot root, ISCMachineRoot selectedMachine, IProgressMonitor pMonitor) throws CoreException {
//		FormulaFactory factory = FormulaFactory.getDefault();
//		
//		// Preliminary Task 1
//		Expression varsType = EB4EBAstUtils.makeProductType(factory, selectedMachine.getSCVariables());
//		
//		// Preliminary Task 2
//		FreeIdentifier fid = factory.makeFreeIdentifier("lol", null, factory.makeBooleanType());
//		FreeIdentifier fid2 = factory.makeFreeIdentifier("lol2", null, factory.makeIntegerType());
//		Collection<FreeIdentifier> fids = Arrays.asList(fid, fid2);
//		
//		Expression exp = factory.makeAtomicExpression(Formula.TRUE, null);
//		Expression exp2 = factory.makeIntegerLiteral(BigInteger.ONE, null);
//		Collection<Expression> exps = Arrays.asList(exp, exp2);
//		
//		Assignment assignmentTest = factory.makeBecomesEqualTo(fids, exps, null);
//		
//		ITypeEnvironmentBuilder envBuilder = factory.makeTypeEnvironment();
//		
//		envBuilder.add(fid);
//		envBuilder.add(fid2);
//		assignmentTest.typeCheck(envBuilder);
//		
//		System.out.println(assignmentTest.getBAPredicate());
//		System.out.println(assignmentTest.getBAPredicate().getBoundIdentifiers().length);
//		for (BoundIdentifier bi : assignmentTest.getBAPredicate().getBoundIdentifiers()) {
//			System.out.println(bi);
//		}
//		
//		Assignment assignmentTest2 = factory.makeBecomesSuchThat(fids, Arrays.asList(fid.asPrimedDecl(), fid2.asPrimedDecl()), assignmentTest.getBAPredicate(), null);
//		
//		selectedMachine.getSCEvents()[0].getSCActions()[0].getAssignment(selectedMachine.getTypeEnvironment());
//		
//		
//		System.out.println(assignmentTest2);
//		try {
//		
//		System.out.println(EB4EBAstUtils.makeBABecomeSuchThat(factory, assignmentTest));
//		System.out.println(EB4EBAstUtils.makeBABecomeSuchThat(factory, assignmentTest, assignmentTest2));
//		
//		for (ISCEvent event : selectedMachine.getSCEvents()) {
//			System.out.println(event);
//			System.out.println(getActionsComprehension(factory, event));
//		}
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		IConstant constant  = root.createChild(IConstant.ELEMENT_TYPE, null, pMonitor);
//		constant.setIdentifierString("testset", pMonitor);
//		
//		IAxiom axiomTest = root.createChild(IAxiom.ELEMENT_TYPE, null, pMonitor);
//		axiomTest.setLabel("test", pMonitor);
//		
//		Predicate test = factory.makeRelationalPredicate(Formula.IN, factory.makeFreeIdentifier("testset", null), varsType, null);
//		axiomTest.setPredicateString(test.toString(), pMonitor);
//	}
//	
//	private static Predicate getActionsComprehension(FormulaFactory factory, ISCEvent event) throws CoreException {
//		ISCMachineRoot mch = (ISCMachineRoot) event.getRoot();
//		
//		List<FreeIdentifier> remainingFids = new ArrayList<FreeIdentifier>();
//		for (ISCVariable var : mch.getSCVariables()) {
//			FreeIdentifier fid = var.getIdentifier(factory);
//			remainingFids.add(fid);
//		}
//		
//		List<Assignment> assignments = new ArrayList<Assignment>();
//		for (ISCAction action : event.getSCActions()) {
//			Assignment assignment = action.getAssignment(mch.getTypeEnvironment());
//			assignments.add(assignment);
//			remainingFids.removeAll(Arrays.asList(assignment.getAssignedIdentifiers()));
//		}
//		
//		for (FreeIdentifier fid : remainingFids) {
//			assignments.add(factory.makeBecomesEqualTo(fid, fid, null));
//		}
//		
//		Predicate pred = EB4EBAstUtils.makeCombinedBAs(factory, assignments.toArray(new Assignment[0]));
//		
//		IFormulaRewriter primeRewriter = new DefaultRewriter(false) {
//			@Override
//			public Expression rewrite(FreeIdentifier identifier) {
//				if(!identifier.isPrimed())
//					return super.rewrite(identifier);
//				return factory.makeFreeIdentifier(identifier.withoutPrime().getName() + "p", identifier.getSourceLocation(), identifier.getType());
//			}
//		};
//		
//		return pred.rewrite(primeRewriter);
//	}
	
}
