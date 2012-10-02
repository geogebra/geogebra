package geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.voronoicell;

import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;

import java.util.Collection;

public class BestVoronoiCells {
    
    /* ***************************************************** */
    // Variables
    
    private int numberstored;
    
    private double[]       bestareas;
    private VVoronoiCell[] bestcells;
    
    /* ***************************************************** */
    // Constructor
    
    public BestVoronoiCells(int number) {
        bestareas = new double[number];
        bestcells = new VVoronoiCell[number];
    }
    public BestVoronoiCells(int number, Collection<VPoint> voronoicells) {
        this(number);
        findBest(voronoicells);
    }
    
    /* ***************************************************** */
    // Getters
    
    public int getBestStored() {
        return numberstored;
    }
    
    public double getBestArea(int index) {
        return bestareas[index];
    }
    
    public VVoronoiCell getBestCell(int index) {
        return bestcells[index];
    }
    
    public double getTotalAreaOfBest() {
        double sum = 0;
        for ( int x=0 ; x<numberstored ; x++ ) sum += bestareas[x];
        return sum;
    }

    public double getAverageArea() {
        double sum = 0;
        if ( numberstored==0 ) return -1.0;
        for ( int x=0 ; x<numberstored ; x++ ) sum += bestareas[x];
        return sum/(numberstored);
    }
    
    public int getAverageX() {
        int tmp = 0;
        if ( numberstored==0 ) return -1;
        for ( int x=0 ; x<numberstored ; x++ ) tmp += bestcells[x].x;
        return tmp/numberstored;
    }
    
    public int getAverageY() {
        int tmp = 0;
        if ( numberstored==0 ) return -1;
        for ( int x=0 ; x<numberstored ; x++ ) tmp += bestcells[x].y;
        return tmp/numberstored;
    }
    
    /* ***************************************************** */
    // Find Best method
    
    public void findBest(Collection<VPoint> voronoicells) {
        // Collect results
        numberstored = 0;
        double area;
        int index, tmp;
        VVoronoiCell cell;
        for ( VPoint point : voronoicells ) {
            // Get cell and cell area
            cell = (VVoronoiCell) point;
            area = cell.getAreaOfCell();
            
            // If area not given, then continue
            if ( area<0 ) continue;
            
            // Consider if best
            if ( numberstored==0 ) {
                numberstored = 1;
                bestareas[0] = area;
                bestcells[0] = cell;
            } else {
                // Find index where to insert
                index = numberstored;
                while ( bestareas[index-1]>area ) {
                    if ( index>1 ) {
                        index--;
                        continue;
                    }
                    
                    // Otherwise, insert in first position and break
                    index = 0;
                    break;
                }
                
                // Only insert if would fit in our structure
                if ( index<bestareas.length ) {
                    // Setup for next
                    if ( numberstored<bestareas.length ) {
                        tmp = numberstored;
                        
                        // Increment for next (when value inserted)
                        numberstored++;
                    } else {
                        tmp = bestareas.length - 1;
                    }
                    
                    // Shift everything right of index
                    for (   ; tmp>index ; tmp-- ) {
                        bestareas[tmp] = bestareas[tmp-1];
                        bestcells[tmp] = bestcells[tmp-1];
                    }
                    
                    // Add new values to index
                    bestareas[index] = area;
                    bestcells[index] = cell;
                }
            }
        }
        
        // Clear remaining values
        for ( tmp=numberstored ; tmp<bestareas.length ; tmp++ ) {
            bestareas[tmp] = 0.0;
            bestcells[tmp] = null;
        }
    }
    
    /* ***************************************************** */
}
