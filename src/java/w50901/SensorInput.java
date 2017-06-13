package w50901;

/**
 * Created by isalnikov on 6/13/2017.
 */
interface SensorInput {
    //---------------------------------------------------------------------------
    // This function sends see information
    public void see(VisualInfo info);

    //---------------------------------------------------------------------------
    // This function receives hear information from player
    public void hear(int time, int direction, String message);

    //---------------------------------------------------------------------------
    // This function receives hear information from referee
    public void hear(int time, String message);
}
