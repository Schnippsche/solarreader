package de.schnippsche.solarreader.backend.automation;

import de.schnippsche.solarreader.backend.automation.actions.Action;

import java.util.ArrayList;
import java.util.List;

public class ActionList
{
  private final List<Action> actions;

  public ActionList()
  {
    this.actions = new ArrayList<>();
  }

  public void addAction(Action action)
  {
    this.actions.add(action);
  }

  public int getSize()
  {
    return this.actions.size();
  }

  public List<Action> getActions()
  {
    return this.actions;
  }

  public void doActions()
  {
    if (actions.isEmpty())
    {
      return;
    }
    for (Action action : actions)
    {
      action.doAction();
    }
  }

}
