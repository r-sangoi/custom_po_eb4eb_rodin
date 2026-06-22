/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

/**
 * This class provides access to the database attributes defined in this plug-in.
 * 
 * @author maamria
 * 
 */
public class TheoryAttributes {

	public static IAttributeType.Boolean ASSOCIATIVE_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".associative");

	public static IAttributeType.Boolean COMMUTATIVE_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".commutative");

	public static IAttributeType.String FORMULA_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".formula");
	// true if expression, false if predicate
	public static IAttributeType.Boolean FORMULA_TYPE_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".formulaType");

	public static IAttributeType.String NOTATION_TYPE_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".notationType");

	public static IAttributeType.String TYPE_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".type");

	public static IAttributeType.Boolean COMPLETE_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".complete");

	public static IAttributeType.String DESC_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".desc");
	
	public static IAttributeType.Boolean DEFINITIONAL_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".definitional");
	
	public static IAttributeType.String GIVEN_TYPE_ATTRIBUTE = RodinCore
		.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".givenType");
	
	public static IAttributeType.Boolean HAS_ERROR_ATTRIBUTE = RodinCore
	 	.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".hasError");
	
	public static IAttributeType.String REASONING_TYPE_ATTRIBUTE = RodinCore
		.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".reasoningType");
	
	public static IAttributeType.String GROUP_ID_ATTRIBUTE = RodinCore
		.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".groupID");
	
	public static IAttributeType.Handle IMPORT_THEORY_PROJECT_ATTRIBUTE = RodinCore
			.getHandleAttrType(TheoryPlugin.PLUGIN_ID + ".importTheoryProject");
	
	public static IAttributeType.Handle IMPORT_THEORY_ATTRIBUTE = RodinCore
		.getHandleAttrType(TheoryPlugin.PLUGIN_ID + ".importTheory");
	
	public static IAttributeType.String INDUCTIVE_ARGUMENT_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".inductiveArgument");
	
	public static IAttributeType.Boolean OUTDATED_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".outdated");
	
	public static IAttributeType.String MODIFICATION_Hash_Value_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".modificationHashValue");
	
	public static IAttributeType.Integer ORDER_ATTRIBUTE = RodinCore
		.getIntegerAttrType(TheoryPlugin.PLUGIN_ID + ".order");
	
	public static IAttributeType.String WD_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".wd");
	
	public static IAttributeType.String APPLICABILITY_ATTRIBUTE = RodinCore
		.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".applicability");
	
	public static IAttributeType.Boolean HYP_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".hyp");

	public static IAttributeType.Handle AVAILABLE_THEORY_ATTRIBUTE = RodinCore
			.getHandleAttrType(TheoryPlugin.PLUGIN_ID + ".availableTheory");
	
	public static IAttributeType.Handle THEORY_PROJECT_ATTRIBUTE = RodinCore
			.getHandleAttrType(TheoryPlugin.PLUGIN_ID + ".availableTheoryProject");

}
