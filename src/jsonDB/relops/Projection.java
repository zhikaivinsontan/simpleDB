package jsonDB.relops;

import java.util.ArrayList;
import java.util.List;

import jsonDB.data.Field;
import jsonDB.data.Row;
import jsonDB.validation.Schema;

public class Projection extends Iterator {
	
	Iterator current_row_i;
	List<String> proj_fields;
	Row current_row;
	int[] proj_field_index;

	Schema child_schema;
	
	/*
	 * Constructor for class Projection. Given an underlying Iterator and a set of fields to project
	 * on, initialize the state of the Projection Iterator and set the schema field to the appropriate
	 * projected schema.
	 */
	public Projection(Iterator child, List<String> projectFields) {
		//TODO
		current_row_i = child;
		proj_fields = projectFields;
		child_schema = child.getSchema();	


		setSchema(child_schema);

		proj_field_index = new int[projectFields.size()];
		for (int i = 0; i < projectFields.size(); i++) {
			for (int j = 0; j < child.schema.getFields().size(); j++) {

				if(projectFields.get(i).equals(child.schema.getFields().get(j).getFieldName())) {
					proj_field_index[i] = j;
					break;
				}
			}			
		}	
	
	}

	public void close() {
		//TODO
		current_row_i.close();
		current_row_i = null;
		current_row = null;
	}
	
	public boolean isOpen() {
		//TODO

		if(current_row_i != null) return true;
		return false;
	}

	public void reset() {
		//TODO
		current_row_i.reset();
	}

	/*
	 * (non-Javadoc)
	 * @see jsonDB.relops.Iterator#hasNext()
	 * 
	 * Compute the next row returned by the underlying Iterator and project it so that it is under
	 * the proper projected Schema.
	 * 
	 * Return true if rows remain, false otherwise.
	 */
	public boolean hasNext() {
		//TODO
		if(!current_row_i.hasNext()) return false;

		Row original = current_row_i.getNext();
		List<Object> row_values = original.getValues();

		List<Object> mod_row_values = new ArrayList<>();

		
		for (int i = 0; i < proj_field_index.length; i++) {
			//System.out.println(">> " + proj_field_index[i]);			
			mod_row_values.add(row_values.get(proj_field_index[i]));
		}		
		
		Row mod_row = new Row();
		mod_row.setValues(mod_row_values);
		current_row = mod_row;

		
		return true;
	}

	
	public Row getNext() {
		//TODO
		return current_row;
	}
	
	/*
	 * Create a new Schema that contains only the columns that are specified in the list of 
	 * projected columns. This schema should not be stored permanently in the
	 * schema catalog and should only be used as a temporary schema for the output of a query.
	 * 
	 * It is the caller's responsibility to validate the given field names under this Schema.
	 */
	public static Schema project(Schema s, List<String> fieldNames)
	{
		//TODO
		List<Field> ps_fields = new ArrayList<>();

		//check if the fieldNmw




		//assuming the project schema needs to have to order of fieldNames
		List<Field> schema_field = s.getFields();
		for (int i = 0; i < fieldNames.size(); i++) {
			for(int j = 0; j < schema_field.size();j++) {
				if(schema_field.get(j).getFieldName().equals(fieldNames.get(i))) {
					ps_fields.add(schema_field.get(j));
					break;
				}
			}						
		}

		Schema project_schema = new Schema(ps_fields);
		return project_schema;
	}
}
