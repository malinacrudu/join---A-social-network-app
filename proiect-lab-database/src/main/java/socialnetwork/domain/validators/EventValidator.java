package socialnetwork.domain.validators;

import socialnetwork.domain.Event;
import socialnetwork.exceptions.ValidationException;

public class EventValidator implements Validator<Event> {
/**
 * Validates a given user.
 *
 * @param entity the given User
 * @throws socialnetwork.exceptions.ValidationException when the entity is considered invalid by the given criteria
 */
@Override
public void validate(Event entity)throws ValidationException
        {
        String exception="";
        if(entity.getDescription()==null||entity.getDescription().equals(""))
            exception+="Event's description cannot be empty!\n";
        if(entity.getTitle()==null||entity.getTitle().equals(""))
            exception+="Event's title cannot be empty!\n";
        if(entity.getStart()==null)
            exception+="Event's starting time cannot be null!\n";
        if(entity.getEnd()==null)
            exception+="Event's ending time cannot be null!\n";
        if(entity.getCreator()==null)
            exception+="Event's creator cannot be null!\n";
        if(!exception.equals(""))
        throw new ValidationException(exception);
        }
        }