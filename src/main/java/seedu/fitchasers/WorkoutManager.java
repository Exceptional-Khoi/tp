package seedu.fitchasers;

import java.util.ArrayList;
public class WorkoutManager
{
    private ArrayList<Workout> workouts =  new ArrayList<>();

    public void addWorkout(String name, int duration )
    {
        workouts.add(new Workout(name, duration));
    }

    public boolean removeWorkout(String name)
    {
        for( Workout w : workouts){
            if(w.getName().equals(name)){
                workouts.remove(w);
                return true;
            }
        }
        return false;
    }
}
