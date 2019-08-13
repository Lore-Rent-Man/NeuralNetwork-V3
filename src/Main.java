import java.io.*;
import org.ejml.simple.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Main {
	
	public static final int epoch_size = 2000;
	public static final int mini_size = 10;

    public static void main(String[] args) {
    	//Modify neuron layer count
    	int[] layers = {784, 30, 10};
    	NeuralNetwork main = new NeuralNetwork(layers);
    	readFile rF = new readFile();     
    	readTestFile rTF = new readTestFile();
    	
		for (int epoch = 1; epoch <= 60000/epoch_size; epoch++) {
			ArrayList<ArrayList<SimpleMatrix>> eOutputs = rF.getEpochLabels(mini_size, epoch_size, epoch);
	    	ArrayList<ArrayList<SimpleMatrix>> eInputs = rF.getImages(mini_size, epoch_size, epoch);
			
			for (int mini_batch = 0; mini_batch < epoch_size/mini_size; mini_batch++) {
	    		ArrayList<SimpleMatrix> intendedOutputs = eOutputs.get(mini_batch);
	    		ArrayList<SimpleMatrix> inputs = eInputs.get(mini_batch);
	    		if (intendedOutputs.size() == inputs.size()) {
	    			main.updateWeights(3, intendedOutputs, inputs);
	    		}
	    	}
			System.out.println("Epoch " + epoch + " complete");
			System.out.println(main.testWeights(rTF.expected, rTF.inputs));
			main.resetCounter();
		}
    	//Initial testing
		
    }
}