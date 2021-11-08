package cz.martin.mnistfileparser;

import java.awt.Image;

public class TrainingImage
{
  private Image image;
  private int label;

  public TrainingImage ( Image image , int label )
  {
    this.image = image;
    this.label = label;
  }

  
  
  public TrainingImage ( Image img )
  {
    image = img;
  }

  public TrainingImage ( int label )
  {
    this . label = label;
  }
  
  public Image getImage ()
  {
    return image;
  }

  public int getLabel ()
  {
    return label;
  }

  public void setImage ( Image image )
  {
    this.image = image;
  }

  public void setLabel ( int label )
  {
    this.label = label;
  }
  
}
