package jsonDB.relops;

import java.util.List;

import jsonDB.data.Predicate;
import jsonDB.data.Row;

import jsonDB.relops.FileScan;

public class Selection extends Iterator {
	

	Iterator current_row_i;
	List<Predicate> c_r_predicate;
	Row current_row;

	/*
	 * Constructor for class Selection. Initialize state for Selection Iterator.
	 * 
	 * Predicates are implemented in Conjunctive Normal Form, with each separate clause of
	//the CNF being represented by one Selection Iterator.
	//i.e. Implement AND with separate Selection Operators chained together. Implement OR by passing
	//all ORed predicates in a CNF clause into one Selection Operator.
	 */
	public Selection(Iterator child, List<Predicate> predicates) {
		//TODO
		setSchema(child.getSchema());
		current_row_i = child;
		c_r_predicate = predicates;
	}

	public void close() {
		//TODO
		current_row_i.close();
		current_row = null;
		current_row_i = null;
		
	}
	
	public boolean isOpen() {
		//TODO

		if(current_row_i == null) return false;
		return true;
	}
	
	public void reset() {
		//TODO
		current_row_i.reset();
	}

	/*
	 * (non-Javadoc)
	 * @see jsonDB.relops.Iterator#hasNext()
	 * 
	 * Compute the next row from the underlying Iterator that satisfies the provided Predicates.
	 * All Predicates within a single Selection operator should be ORed together - if the row
	 * satisfies at least one Predicate then the row is accepted as a valid result. Implement AND
	 * by chaining together two (or more) Selection Iterators.
	 */
	public boolean hasNext() {
		//TODO
		while (true) {
			if (!current_row_i.hasNext()) return false;	
			
			current_row = current_row_i.getNext();

			boolean found = false;
			//evaluate against each predicate and stop when you find one
			for (int i = 0; i < c_r_predicate.size(); i++) {
				if(c_r_predicate.get(i).evaluate(this.getSchema(), current_row)) {
					found = true;
					break;
				}
			}	
			
			if(found == true) {
				return true;
			}
		} 
	}

	
	public Row getNext() {
		//TODO
		return current_row;
	}

}
