package weka;
import java.util.ArrayList;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


import weka.filters.Filter;
import weka.filters.supervised.instance.StratifiedRemoveFolds;
 
public class Tree {
	Instances data;
	 Evaluation eval;
	 J48 tree;
	
	public Tree(boolean prun, String classifier_attr,int division,boolean reduced_error_prunning,boolean confidence, float confidence_perc) throws Exception {
		 DataSource source ;
	if(classifier_attr.equals("Family"))
		source=new DataSource("class_familia.arff");
	else if(classifier_attr.equals("Species"))
	  source = new DataSource("class_especie.arff");
	else
		source=new DataSource("class_genero.arff");
	
	  data = source.getDataSet();

	 
	
	 // Set class to last attribute
	 if (data.classIndex() == -1)
	     data.setClassIndex(data.numAttributes() - 1);

	 // use StratifiedRemoveFolds to randomly split the data  
	 StratifiedRemoveFolds filter = new StratifiedRemoveFolds();

	 // set options for creating the subset of data
	 String[] options = new String[6];

	 options[0] = "-N";                 // indicate we want to set the number of folds                       
	 options[1] = Integer.toString(division);  // split the data into n random folds
	 options[2] = "-F";                 // indicate we want to select a specific fold
	 options[3] = Integer.toString(1);  // select the first fold
	 options[4] = "-S";                 // indicate we want to set the random seed
	 options[5] = Integer.toString(1);  // set the random seed to 1

	 filter.setOptions(options);        // set the filter options
	 filter.setInputFormat(data);       // prepare the filter for the data format    
	 filter.setInvertSelection(false);  // do not invert the selection

	 
	 
	 // apply filter for test data here
	 Instances test = Filter.useFilter(data, filter);

	 //  prepare and apply filter for training data here
	 filter.setInvertSelection(true); 
	 Instances train = Filter.useFilter(data, filter);
	 // invert the selection to get other data 
	  tree = new J48();
	  System.out.println(prun);
	  tree.setUnpruned(prun);
	  ArrayList<String> tree_options= new ArrayList();
	  if(reduced_error_prunning && !prun) {
		  tree_options.add("-R");
		  System.out.println("reduced error");
		  tree.setOptions(tree_options.toArray(new String[0]));
	  }
	  else if(confidence && !prun && confidence_perc>0 && confidence_perc <=0.5 ) {
		  System.out.println("confidence");
		  tree_options.add("-C");
		  String temp=""+confidence_perc;
		  tree_options.add(temp);
		  tree.setOptions(tree_options.toArray(new String[0]));
	  }
	  
	  

	  
	 tree.buildClassifier(train);
	 // evaluate classifier and print some statistics
	 eval = new Evaluation(train);
	 
	 eval.evaluateModel(tree, test);
	
	
	// System.out.println(tree.toString());
	
  }
	
	public J48 getTree() {
		return tree;
	}

	public String getResults() throws Exception {
		String temp="";
		 temp+=data.toSummaryString();
		 temp+='\n';
		 temp+=eval.toSummaryString("\nResults\n======\n", false);
		 temp+='\n';
		 temp+=eval.toMatrixString("\nMatrix\n======\n");
		 temp+='\n';
		 temp+=tree.toString();
		 temp+=eval.toClassDetailsString("\nClass Details\n======\n");
		 temp+='\n';
		 return temp; 
	}
	
	public static void main(String [] args) throws Exception {
		Tree t = new Tree(false,"family",5,false,false,0);
		t.getResults();
	}
}