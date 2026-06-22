package org.eventb.theory.ui.explorer;
import static fr.systerel.internal.explorer.statistics.StatisticsUtil.getParentLabelOf;

import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.ui.explorer.model.TheoryModelPOContainer;
import org.eventb.theory.ui.explorer.model.TheoryModelController;
import org.eventb.theory.ui.explorer.model.TheoryModelProject;


import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.statistics.IStatistics;

/**
 * This class represents a simple statistics that is not aggregated. 
 * 
 * @see fr.systerel.internal.explorer.statistics.IStatistics
 *
 */
@SuppressWarnings("restriction")
public class Statistics implements IStatistics{

	private Object parent;
	private int total;
	private int undischarged;
	private int manual;
	private int reviewed;
	
	public Statistics(Object parent) {
		this.parent = parent;
		calculate();
	}
	
	/**
	 * Calculates the statistics from the given parent.
	 */
	public void calculate() {
		if (parent instanceof TheoryModelPOContainer) {
			final TheoryModelPOContainer container = (TheoryModelPOContainer) parent;
			total = container.getPOcount();
			undischarged = container.getUndischargedPOcount();
			manual = container.getManuallyDischargedPOcount();
			reviewed = container.getReviewedPOcount();
		}
		if (parent instanceof TheoryModelProject) {
			final TheoryModelProject project = (TheoryModelProject) parent;
			total = project.getPOcount();
			undischarged = project.getUndischargedPOcount();
			manual = project.getManuallyDischargedPOcount();
			reviewed = project.getReviewedPOcount();
		}
		if (parent instanceof IElementNode) {
			final IElementNode node = (IElementNode) parent;
			TheoryModelPOContainer cont =  null;
			if (node.getParent() instanceof ITheoryRoot) {
				cont = TheoryModelController.getTheory((ITheoryRoot) node.getParent());
			}
			if (cont != null) {
				total =  cont.getPOcount(node.getChildrenType());
				undischarged = cont.getUndischargedPOcount(node.getChildrenType());
				manual = cont.getManuallyDischargedPOcount(node.getChildrenType());
				reviewed = cont.getReviewedPOcount(node.getChildrenType());
			}
		}
	}
	
	@Override
	public int getTotal(){
		return total;
	}

	@Override
	public int getUndischarged(){
		return undischarged;
	}
	
	@Override
	public int getManual(){
		return manual;
	}

	@Override
	public int getAuto(){
		return total - undischarged -manual;
	}
	
	@Override
	public int getReviewed(){
		return reviewed;
	}
	
	/**
	 * 
	 * @return the number of Proof Obligations that are undischarged but not reviewed
	 */
	@Override
	public int getUndischargedRest() {
		return undischarged- reviewed;
	}
	
	@Override
	public String getParentLabel() {
		return getParentLabelOf(parent);
	}

	@Override
	public boolean isAggregate() {
		return false;
	}

	@Override
	public Object getParent() {
		return parent;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + manual;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + reviewed;
		result = prime * result + total;
		result = prime * result + undischarged;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Statistics other = (Statistics) obj;
		if (manual != other.manual)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (reviewed != other.reviewed)
			return false;
		if (total != other.total)
			return false;
		if (undischarged != other.undischarged)
			return false;
		return true;
	}


	@Override
	public void buildCopyString(StringBuilder builder, boolean copyLabel,
			Character separator) {
		if (copyLabel) {
			builder.append(getParentLabel()) ;
			builder.append(separator);
		}
		builder.append(getTotal());
		builder.append(separator);
		builder.append(getAuto());
		builder.append(separator);
		builder.append(getManual());
		builder.append(separator);
		builder.append(getReviewed());
		builder.append(separator);
		builder.append(getUndischargedRest());
		builder.append(System.getProperty("line.separator"));
	}
}
