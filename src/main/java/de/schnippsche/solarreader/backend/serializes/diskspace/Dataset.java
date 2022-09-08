package de.schnippsche.solarreader.backend.serializes.diskspace;

import java.util.ArrayList;

public class Dataset
{
  public final ArrayList<String> data;
  public final ArrayList<String> backgroundColor;
  public String label;

  public Dataset()
  {
    this("");
  }

  public Dataset(String label)
  {
    this.label = label;
    data = new ArrayList<>();
    backgroundColor = new ArrayList<>();
  }

}
