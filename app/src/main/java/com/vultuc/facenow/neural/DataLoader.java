package com.vultuc.facenow.neural;


import com.vultuc.facenow.matrix.Matrix;

import java.io.IOException;

public interface DataLoader {

    /**
     * @param xIDs Some type of id numbers for the datapoints to load.
     * @return Training data for the specified ids
     * @throws IOException
     */
    public Matrix loadData(Matrix xIDs) throws IOException;

    /**
     * @return Number of values for each datapoint
     */
    public int getDataSize();

}
