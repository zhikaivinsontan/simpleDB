package jsonDB.validation;


import jsonDB.data.Row;
import jsonDB.data.Predicate;
import jsonDB.data.Predicate.*;
import java.util.List;

public class CheckConstraintValidator implements ConstraintValidator{
	
	List<Predicate> predicate_list;
	Schema nimama;	

	/*
	 * Constructor for class CheckConstraintValidator, given the Constraint and the
	 * Schema the constraint applies on. Initializes ConstraintValidator state.
	 */
	public CheckConstraintValidator(Constraint constraint, Schema s) throws IllegalConstraintException {
		//TODO
		
		predicate_list = constraint.getPredicates();	
		nimama = s;

	}
	
	/*
	 * Checks to see if the given Row satisfies the given CHECK constraint. Returns true if all
	 * predicates are satisfied, otherwise returns false (assume for simplicity that no OR
	 * boolean operators are allowed in CHECK Constraints - all Predicates must be connected
	 * with AND).
	 */
	public boolean validateConstraint(Row r) {
		// TODO
		
		for (int i = 0; i < predicate_list.size(); i++) {
			if(!predicate_list.get(i).evaluate(nimama, r)) return false;
		}
		
		return true;
	}

	
}
