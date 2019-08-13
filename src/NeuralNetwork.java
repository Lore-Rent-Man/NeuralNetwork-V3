
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.ejml.simple.*;

//Cost function: C = 0.5(y(x) - a)^2
//Cost function per neuron: L = partial derv(cost/activation) * derv(activation/neuron)

public class NeuralNetwork {
	
	private int numLayers;
	private int[] layerSizes;
	ArrayList<SimpleMatrix> biasg = new ArrayList<SimpleMatrix>();
	ArrayList<SimpleMatrix> weightg = new ArrayList<SimpleMatrix>();
	ArrayList<SimpleMatrix> biases = new ArrayList<SimpleMatrix>();
	ArrayList<SimpleMatrix> weights = new ArrayList<SimpleMatrix>();
	ArrayList<SimpleMatrix> layerOutputs = new ArrayList<SimpleMatrix>();
	ArrayList<SimpleMatrix> activatedOutputs = new ArrayList<SimpleMatrix>();
	ArrayList<SimpleMatrix> dadn = new ArrayList<SimpleMatrix>();
	int ebatch_total = 0;
	int enum_correct = 0;
		
	public NeuralNetwork(int[] nnSize) {
		numLayers = nnSize.length;
		layerSizes = nnSize;
		//Initialize weights and biases with normal distribution values. Skipping first layer as that is input layer.
		for (int layer = 1; layer < nnSize.length; layer++) {
			biases.add(genRandMatrix(nnSize[layer], 1));
			weights.add(genRandMatrix(nnSize[layer], nnSize[layer - 1]));
		}
	}
	
	//feedForward method. Generates output of neuralnetwork, must be run to initialize arrays used for calculus equations
	public SimpleMatrix feedForward(SimpleMatrix input) {
		if (input.numRows() != layerSizes[0]) throw new IllegalArgumentException("Input matrix does not match NN structure");
		layerOutputs.clear();
		activatedOutputs.clear();
		dadn.clear();
		activatedOutputs.add(input);
		for (int layer = 0; layer < weights.size(); layer++) {
			SimpleMatrix w = weights.get(layer);
			SimpleMatrix b = biases.get(layer);
			input = output(w, b, input);
		}
		return input;
	}
	
	public SimpleMatrix output(SimpleMatrix weights, SimpleMatrix bias, SimpleMatrix inputs) {
		//output is matrix containing neuron layer values before activation
		//actOutput is matrix containing neuron layer values after activation
		//sigPrimeOutput is matrix containing values representing change in activation with respect to the neuron function
		
		//input neurons do not have weights
				
		//Because the library can only do vector dot product, I had to separate every row and take the dot product of it with the inputs
		/*for (int i = 0; i < weights.numRows(); i++) {
			double fValue = weights.extractVector(true, i).dot(inputs.transpose());
			output.set(i, 0, fValue);
			actOutput.set(i, 0, sigmoid(fValue));
			sigPrimeOutput.set(i, 0, sigmoidPrime(fValue));
		}*/
		SimpleMatrix fMatrix = weights.mult(inputs);
		fMatrix = fMatrix.plus(bias);
		SimpleMatrix actMatrix = new SimpleMatrix(fMatrix);
		SimpleMatrix primeMatrix = new SimpleMatrix(fMatrix);
		layerOutputs.add(fMatrix);
		for (int row = 0; row < fMatrix.numRows(); row++) {
			actMatrix.set(row, sigmoid(fMatrix.get(row)));
			primeMatrix.set(row, sigmoidPrime(fMatrix.get(row)));
		}
		activatedOutputs.add(actMatrix);
		dadn.add(primeMatrix);
		return actMatrix;
	}
	
	public String testWeights(ArrayList<SimpleMatrix> y, ArrayList<SimpleMatrix> inputs) {
		for (int i = 0; i < inputs.size(); i++) {
			SimpleMatrix x = feedForward(inputs.get(i));
			double max = x.get(0);
			int index = 0;
			for (int xm = 0; xm < x.numRows();xm++) {
				if (x.get(xm) > max) {
					index = xm;
					max = x.get(xm);
				}
			}
			ebatch_total++;
			if (y.get(i).get(index) == 1) {
				enum_correct++;
			}
		}
		return enum_correct + "/" + ebatch_total;
	}
	
	public void updateWeights(double learningRate, ArrayList<SimpleMatrix> y, ArrayList<SimpleMatrix> inputs) {
		biasg.clear();
		weightg.clear();
		for (int layer = 1; layer < numLayers; layer++) {
			biasg.add(zeroMatrix(layerSizes[layer], 1));
			weightg.add(zeroMatrix(layerSizes[layer], layerSizes[layer - 1]));
		}
		for (int i = 0; i < y.size(); i++) {
			SimpleMatrix x = feedForward(inputs.get(i));
			SimpleMatrix layerError = x.minus(y.get(i)).elementMult(dadn.get(dadn.size() - 1));
			biasg.set(biasg.size() - 1, biasg.get(biasg.size() - 1).plus(layerError));
			//initial error multiplied with activated output of previous layer
			SimpleMatrix weightGra = layerError.mult(activatedOutputs.get(activatedOutputs.size() - 2).transpose());
			weightg.set(weightg.size() - 1, weightg.get(weightg.size() - 1).plus(weightGra));
			for (int layer = 2; layer < numLayers; layer++) {
				layerError = weights.get(weights.size() - layer + 1).transpose().mult(layerError);
				layerError = layerError.elementMult(dadn.get(dadn.size() - layer));
				biasg.set(biasg.size() - layer, biasg.get(biasg.size() - layer).plus(layerError));
				weightGra = layerError.mult(activatedOutputs.get(activatedOutputs.size() - 1 - layer).transpose());
				weightg.set(weightg.size() - layer, weightg.get(weightg.size() - layer).plus(weightGra));
			}
		}
		
		for (int i = 0; i < weightg.size(); i++) {
			weightg.set(i, weightg.get(i).scale(learningRate/y.size()));
			biasg.set(i, biasg.get(i).scale(learningRate/y.size()));
		}
		
		for(int i = 0; i < weights.size(); i++) {
			weights.set(i, weights.get(i).minus(weightg.get(i)));
			biases.set(i, biases.get(i).minus(biasg.get(i)));
		}
	}
	
	/*public String test_weights(File f, File g) {
		
	}*/
	
	public void resetCounter() {
		enum_correct = 0;
		ebatch_total = 0;
	}
	
	//static methods
	
	public static SimpleMatrix genRandMatrix(int row, int col) {
		Random rand = new Random();
		SimpleMatrix b = new SimpleMatrix(row, col);
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				b.set(r, c, rand.nextGaussian());
			}
		}
		return b;
	}
	
	public static SimpleMatrix zeroMatrix(int row, int col) {
		SimpleMatrix b = new SimpleMatrix(row, col);
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				b.set(r, c, 0);
			}
		}
		return b;
	}
	
	public static double sigmoid(double input) {
		return 1.0/(1.0 + Math.exp(-input));
	}
	
	public static double sigmoidPrime(double input) {
		return sigmoid(input) * (1-sigmoid(input));
	}
}
