package droolsbook.droolsflow.model;

import java.io.Serializable;

public class Rating implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Integer rating;

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }
  
}
