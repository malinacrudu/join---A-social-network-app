package socialnetwork.repository.database;

import socialnetwork.domain.Event;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.exceptions.AlreadyExistsException;
import socialnetwork.exceptions.ValidationException;
import socialnetwork.repository.PaginatedRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EventDbRepository implements PaginatedRepository<Long, Event>
{
    private String url;
    private String username;
    private String password;
    private Validator<Event> validator;
    private Integer pageSize;
    private Integer currentPage;
    private User user;

    public void setUser(User user)
    {
        this.user = user;
    }

    public EventDbRepository(String url, String username, String password, Validator<Event> validator, Integer pageSize)
    {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.pageSize=pageSize;
        this.currentPage=1;
    }

    @Override
    public Event findOne(Long id) {
        if (id==null)
            throw new IllegalArgumentException("The id must be not null!");
        for(Event e:findAll())
            if(e.getId().equals(id))return e;
        return null;
    }


    @Override
    public Iterable<Event> findAll() {
        Set<Event> events = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location= resultSet.getString("location");
                String path= resultSet.getString("path");
                Long creator = resultSet.getLong("creator");
                LocalDate start = LocalDate.parse(resultSet.getString("start_time"));
                LocalDate end = LocalDate.parse(resultSet.getString("end_time"));
                Event e =new Event(title,description,creator,start,end);
                e.setId(id);
                e.setPath(path);
                e.setLocation(location);
                PreparedStatement statement2 = connection.prepareStatement("SELECT * from events_user WHERE id_event=?");
                statement2.setLong(1,id);
                ResultSet resultSet2 = statement2.executeQuery();
                while(resultSet2.next())
                {
                    Long participant = resultSet2.getLong("id_user");
                    e.addParticipant(participant);

                }
                events.add(e);
            }
            return events;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return events;
    }
    public Long findMyEvent(String title, String description, Long creator,String path,String end_time, String start_time) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM events WHERE creator=? AND title=? AND description=? AND path=? AND end_time=? AND start_time=?"))
        {
            statement.setLong(1, creator);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setString(4, path);
            statement.setString(5, end_time);
            statement.setString(6, start_time);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);

        } catch (SQLException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Event save(Event entity)
    {

        if (entity==null)
            throw new IllegalArgumentException("Entity must be not null!");

        Long creator = entity.getCreator();
        String title = entity.getTitle();
        String description=entity.getDescription();
        String location =entity.getLocation();
        String path =entity.getPath();
        String end = entity.getEnd().toString();
        String start = entity.getStart().toString();
        ArrayList<Long> id_users = (ArrayList<Long>) entity.getParticipants();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO events(title,description,location,path,creator,start_time,end_time) VALUES (?,?,?,?,?,?,?)")) {

            statement.setString(1,title);
            statement.setString(2,description);
            statement.setString(3,location);
            statement.setString(6,start);
            statement.setString(7,end);
            statement.setString(4,path);
            statement.setLong(5,creator);
            validator.validate(entity);
            statement.executeUpdate();
            for (Long x : id_users)
            {
                PreparedStatement statement2 = connection.prepareStatement("INSERT INTO events_user(id_event,id_user) VALUES (?,?)");
                Long id_e = findMyEvent(title, description,creator,path,end,start);
                statement2.setLong(1, id_e);
                statement2.setLong(2, x);
                statement2.executeUpdate();
            }
        } catch (ValidationException e)
        {
            throw new ValidationException (e.getMessage());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    public void setPage(Integer page)
    {
        this.currentPage=page;
    }
    @Override
    public Event delete(Long id)
    {
       return null;
    }

    @Override
    public Event update(Event entity)
    {

        if (entity==null)
            throw new IllegalArgumentException("Entity must be not null!");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM events_user WHERE id_event=?"))
        {
            validator.validate(entity);
            statement.setLong(1,entity.getId());
            statement.executeUpdate();
            PreparedStatement statement2 = connection.prepareStatement("INSERT INTO events_user(id_event,id_user) VALUES (?,?)");
            for(Long user : entity.getParticipants())
            {
                statement2.setLong(1, entity.getId());
                statement2.setLong(2, user);
                statement2.executeUpdate();
            }
        }
        catch (ValidationException e)
        {
           throw new ValidationException(e.getMessage());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Iterable<Event> nextPage(User user)
    {
        if(currentPage<totalPages(user))
        {
            currentPage++;
            return getCurrentPage(user);
        }
        return null;
    }


    @Override
    public Iterable<Event> previousPage(User user)
    {
        if(currentPage>1)
        {
            currentPage--;
            return getCurrentPage(user);
        }
        return null;
    }

    private boolean check(Event event, User user)
    {
        Long filter= (long) -1;
        if(user!=null)filter=user.getId();
        if(!event.getCreator().equals(filter))
            for(Long participant : event.getParticipants())
            {
                if(participant.equals(filter))
                    return true;
            }
        else
            return true;
        return false;
    }
    @Override
    public Iterable<Event> getCurrentPage(User user)
    {
        Set<Event> events = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location= resultSet.getString("location");
                String path= resultSet.getString("path");
                Long creator = resultSet.getLong("creator");
                LocalDate start = LocalDate.parse(resultSet.getString("start_time"));
                LocalDate end = LocalDate.parse(resultSet.getString("end_time"));
                Event e =new Event(title,description,creator,start,end);
                e.setId(id);
                e.setPath(path);
                e.setLocation(location);
                PreparedStatement statement2 = connection.prepareStatement("SELECT * from events_user WHERE id_event=?");
                statement2.setLong(1,id);
                ResultSet resultSet2 = statement2.executeQuery();
                while(resultSet2.next())
                {
                    Long participant = resultSet2.getLong("id_user");
                    e.addParticipant(participant);

                }
                events.add(e);
            }
            if(user!=null)
            {
                events.removeIf(m -> !check(m, user));
            }
            return events.stream().skip(pageSize*(currentPage-1)).limit(pageSize).collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Integer getPageNumber()
    {
        return currentPage;
    }

    @Override
    public Integer getPageSize()
    {
        return pageSize;
    }

    @Override
    public Integer totalPages(User user)
    {
        Set<Event> events = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location= resultSet.getString("location");
                String path= resultSet.getString("path");
                Long creator = resultSet.getLong("creator");
                LocalDate start = LocalDate.parse(resultSet.getString("start_time"));
                LocalDate end = LocalDate.parse(resultSet.getString("end_time"));
                Event e =new Event(title,description,creator,start,end);
                e.setId(id);
                e.setPath(path);
                e.setLocation(location);
                PreparedStatement statement2 = connection.prepareStatement("SELECT * from events_user WHERE id_event=?");
                statement2.setLong(1,id);
                ResultSet resultSet2 = statement2.executeQuery();
                while(resultSet2.next())
                {
                    Long participant = resultSet2.getLong("id_user");
                    e.addParticipant(participant);

                }
                events.add(e);
            }
            if(user!=null)
            {
                events.removeIf(m -> !check(m, user));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        Integer total=events.size();
        if(total%pageSize!=0)
            return total/pageSize+1;
        else
            return total/pageSize;
    }

}
