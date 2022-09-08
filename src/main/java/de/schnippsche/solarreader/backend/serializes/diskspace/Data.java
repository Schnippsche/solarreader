package de.schnippsche.solarreader.backend.serializes.diskspace;

import java.util.ArrayList;

public class Data
{
  public final ArrayList<String> labels;
  public final ArrayList<Dataset> datasets;
  public String titletext;

  public Data()
  {
    labels = new ArrayList<>();
    datasets = new ArrayList<>();
    titletext = "";
  }

}
