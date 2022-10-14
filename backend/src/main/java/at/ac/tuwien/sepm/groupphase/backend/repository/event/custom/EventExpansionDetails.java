package at.ac.tuwien.sepm.groupphase.backend.repository.event.custom;

public interface EventExpansionDetails {
  Long getId();

  String getTitle();

  String getDescription();

  String getCategory();

  String getImageReference();

  Boolean getSoldOut();
}
