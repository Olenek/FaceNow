package com.vultuc.facenow.neural;

import java.io.Serializable;


public class NNParams implements Serializable {

    public NNParams() {
    }

    /**
     * Number of categories for each nominal attribute in training data.
     * This array should have the same length as the number of columns in training data.
     * For each nominal attribute, the value should be equal to the number of categories of that attribute.
     * For each numeric attribute, the value should be 1.
     * If numCategories is null, all attributes in x will be interpreted as numeric.
     * <p>
     * Default is null.
     */
    public int[] numCategories = null;

    /**
     * Number of classes, if classification.
     * 1 if regression.
     * <p>
     * Default is 1
     */
    public int numClasses = 1;


    /**
     * Number of channels in the input. (e.g. 3 channels for RGB-images)
     * Only used for convolutional NNs
     * <p>
     * Default is 1
     */
    public int numInputChannels = 1;


    /**
     * Width of the input.
     * For convolution, we have to know the width/height of the input images. (Since they come flattened.)
     * If inputWidth is set to zero, a quadratic image with equal width and height is presumed.
     * Only used for convolutional NNs.
     * <p>
     * Default is 0
     */
    public int inputWidth = 0;


    /**
     * Number of units in each hidden layer.
     * Also, patch-size and pooling-size for convolutional layers.
     * <p>
     * Default is a single fully connected layer with 100 hidden units.
     */
    public NNLayerParams[] hiddenLayerParams = new NNLayerParams[]{new NNLayerParams(100)};


    /**
     * L1 weight penalty.
     * Even when using dropout, it may be a good idea to have a small weight penalty, to keep weights down and avoid overflow.
     * <p>
     * Default is 1E-8
     */
    public double weightPenalty = 1E-8;


    /**
     * Initial learning rate.
     * If 0, different learning rates will be tried to automatically find a good initial rate.
     * <p>
     * Default is 0.
     */
    public double learningRate = 0.0;

    /**
     * Number of examples to use in each mini-batch.
     * Batch-size 100 is a good choice for fully connected networks.
     * Batch-size 1 is a good choice for convolutional networks.
     * If set to 0, a batch-size of 100 or 1 will be used depending on whether the network is fully connected or convolutional.
     * <p>
     * Default is 0.
     */
    public int batchSize = 0;


    /**
     * Max number of iterations (epochs) of training to perform.
     * Training may be halted earlier if convergence criteria is met or training is halted by the user.
     * <p>
     * Default is 1000
     */
    public int maxIterations = 1000;


    /**
     * Number of threads to use for concurrent calculations.
     * If 0, threads will automatically be set to the number of CPU cores found.
     * <p>
     * Default is 0.
     */
    public int numThreads = 0;

    /**
     * Dropout rate of input layer.
     * Only used for fully connected layers.
     * Convolutional layers generally don't need dropout, due to regularization via parameter sharing.
     * Typically set somewhat lower than dropout rate in hidden layer.
     * <p>
     * Default is 0.2.
     */
    public double inputLayerDropoutRate = 0.2;

    /**
     * Dropout rate of hidden layers.
     * Only used for fully connected layers.
     * Convolutional layers generally don't need dropout, due to regularization via parameter sharing.
     * <p>
     * Default is 0.5
     */
    public double hiddenLayersDropoutRate = 0.5;


    /**
     * If true, training progress will be output to the console.
     * Also, the user will be able to halt training by pressing enter in the console.
     * <p>
     * Default is true.
     */
    public boolean debug = true;

    /**
     * If true, will normalize the data in all numeric columns, by subtracting average and dividing by standard deviation.
     * <p>
     * Default is true.
     */
    public boolean normalizeNumericData = true;

    /**
     * If cost hasn't moved by more than the threshold fraction for 10 iterations, training is stopped.
     * <p>
     * Default is 0.02.
     */
    public double convergenceThreshold = 0.02;

    /**
     * If training dataset is too large to fit in memory,
     * you can provide a dataloader that loads data on demand.
     * If dataloader is set to null, all data is expected to be passed to the NN at the start of training.
     * <p>
     * Default is null.
     */
    public transient DataLoader dataLoader = null;


    public static class NNLayerParams implements Serializable {
        public int numFeatures = 0;
        public int patchWidth = 0;
        public int patchHeight = 0;
        public int poolWidth = 0;
        public int poolHeight = 0;

        /**
         * Create a definition of a fully connected layer.
         */
        public NNLayerParams(int numFeatures) {
            this.numFeatures = numFeatures;
        }

        /**
         * Create a definition of a convolutional connected layer.
         */
        public NNLayerParams(int numFeatures, int patchWidth, int patchHeight, int poolWidth, int poolHeight) {
            this.numFeatures = numFeatures;
            this.patchWidth = patchWidth;
            this.patchHeight = patchHeight;
            this.poolWidth = poolWidth;
            this.poolHeight = poolHeight;
        }

        public boolean isConvolutional() {
            return patchWidth > 0 && patchHeight > 0;
        }

        public boolean isPooled() {
            return poolWidth > 1 || poolHeight > 1;
        }
    }
}
