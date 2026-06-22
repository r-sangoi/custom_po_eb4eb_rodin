/**
 * 
 */
package org.eventb.theory.internal.ui;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

/**
 * @author maamria
 * 
 */
public class Messages {
	
	public static String deploy_deploySuccess;
	public static String deploy_deployFailure;
	
	public static String rewriteRule_isComplete;
	public static String rewriteRule_isIncomplete;
	
	public static String inferenceRule_given_isHyp;
	public static String inferenceRule_given_isNotHyp;
	
	public static String operator_isExpression;
	public static String operator_isPredicate;
	public static String operator_isAssociative;
	public static String operator_isNotAssociative;
	public static String operator_isCommutative;
	public static String operator_isNotCommutative;
	
	public static String wizard_deployTitle;
	//public static String wizard_deployDescription;
	public static String wizard_undeployTitle;
	public static String wizard_undeployDescription;
	public static String wizard_undeployPageMessage;
	public static String wizard_deployPage2Message;
	public static String wizard_errorProjMustBeSelected;
	public static String wizard_errorTheoriesMustBeSelected;
	public static String wizard_errorUndefined;
	public static String wizard_deploySuccess;
	
	public static String wizard_errorProjMustBeValid; 
	public static String wizard_errorProjMustBeWritable;
	public static String wizard_errorTheoryNameMustBeSpecified;
	public static String wizard_errorMachineNameClash;
	public static String wizard_errorContextNameClash;
	public static String wizard_errorTheoryNameClash;
	public static String wizard_errorGlobalTheoryNameClash;
	public static String wizard_errorFileClash;
	
	public static String wizard_newTheoryDesc;
	public static String wizard_newTheoryTitle;
	
	//public static String wizard_rebuild;
	public static String wizard_newTheoryPathTitle;
	public static String wizard_multipleTheoryPathError;
	public static String wizardRenameFileExists;
	public static String wizard_newTheoryPathDesc;
	
	private static final String BUNDLE_NAME = "org.eventb.theory.internal.ui.messages"; //$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Do not instantiate
	}

	/**
	 * Bind the given message's substitution locations with the given string
	 * values.
	 * 
	 * @param message
	 *            the message to be manipulated
	 * @param bindings
	 *            An array of objects to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object... bindings) {
		return MessageFormat.format(message, bindings);
	}

}
