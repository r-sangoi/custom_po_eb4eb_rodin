package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ast.Formula;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ParsedFormula;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.states.RewriteRuleLabelSymbolTable;
import org.eventb.theory.core.sc.states.RuleAccuracyInfo;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IRodinElement;

/**
 * A processor module for rewrite rules. It sets the accuracy of processed rules.
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class RewriteRuleModule extends RuleModule<IRewriteRule, ISCRewriteRule>{

	public static final IModuleType<RewriteRuleModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID + ".rewriteRuleModule");
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalRewriteRule(symbol,
				true, element, component);
	}
	
	protected IRewriteRule[] getRuleElements(IRodinElement element)
			throws CoreException {
		IProofRulesBlock rulesBlock = (IProofRulesBlock) element;
		return rulesBlock.getRewriteRules();
	}
	
	protected ILabelSymbolInfo[] fetchRules(IRewriteRule[] rules, String theoryName,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean accurate = true;
		ILabelSymbolInfo[] symbolInfos = new ILabelSymbolInfo[rules.length];
		initFilterModules(repository, monitor);
		for (int i = 0; i < rules.length; i++) {
			symbolInfos[i] = fetchLabel(rules[i], theoryName, monitor);
			if (symbolInfos[i] == null){
				accurate = false;
				continue;
			}
			if (!filterModules(rules[i], repository, monitor)) {
				symbolInfos[i].setError();
				accurate = false;
			}
		}
		endFilterModules(repository, monitor);
		if(!accurate)
			accuracyInfo.setNotAccurate();
		return symbolInfos;
	}
	
	protected void processRules(IRewriteRule[] rules, ISCRewriteRule[] scRules,
			ISCStateRepository repository, ILabelSymbolInfo[] infos,
			IProgressMonitor monitor) throws CoreException {
		for (int i = 0; i < rules.length; i++) {
			IRewriteRule rule = rules[i];
			if (infos[i] != null && !infos[i].hasError()) {
				Formula<?> lhsForm = ModulesUtils.parseAndTypeCheckFormula(rule.getFormula(),
						repository.getFormulaFactory(), repository.getTypeEnvironment());
				boolean ok = (lhsForm != null) && scRules[i] != null;
				if (ok) {
					scRules[i].setSCFormula(lhsForm, monitor);
					// states
					RewriteRuleLabelSymbolTable labelTable = new RewriteRuleLabelSymbolTable(
							ModulesUtils.LABEL_SYMTAB_SIZE);
					repository.setState(labelTable);
					RuleAccuracyInfo ruleAccuracyInfo = new RuleAccuracyInfo();
					repository.setState(ruleAccuracyInfo);
					ParsedFormula lhsParsedFormula = new ParsedFormula();
					lhsParsedFormula.setFormula(lhsForm);
					repository.setState(lhsParsedFormula);
					// call child processors
					initProcessorModules(rule, repository, null);
					processModules(rule, scRules[i], repository, monitor);
					endProcessorModules(rule, repository, null);
					// accuracy
					scRules[i].setAccuracy(ruleAccuracyInfo.isAccurate(), monitor);
				}
				if (!ok) {
					infos[i].setError();
					if(scRules[i] != null)
						scRules[i].setAccuracy(false, monitor);
					accuracyInfo.setNotAccurate();
				}	
			} else{
				accuracyInfo.setNotAccurate();
			}
		}
		monitor.worked(1);
	}

	@Override
	protected String getMessage() {
		return Messages.progress_TheoryRewriteRules;
	}

	@Override
	protected ISCRewriteRule[] createSCRulesArray(int length) {
		return new ISCRewriteRule[length];
	}

	@Override
	protected ISCRewriteRule getSCRule(ILabeledElement scRule) {
		return (ISCRewriteRule) scRule;
	}

	@Override
	protected ILabelSymbolInfo makeLocalRule(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return TheorySymbolFactory.getInstance().makeLocalRewriteRule(symbol, true, element, component);
	}
}
