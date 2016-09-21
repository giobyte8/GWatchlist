package me.gwatchlist.services;

import com.googlecode.objectify.ObjectifyService;
import me.gwatchlist.entities.User;
import me.gwatchlist.entities.UserPreferences;

import java.util.Date;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 *
 * Created by giovanni on 11/09/16.
 */
public class UserService {

    static {
        ObjectifyService.register(User.class);
    }

    public User login(String email, String name) {

        // Create user if does not exists
        User user = this.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setCreatedAt(new Date());
        }

        // Updates name and last login date
        user.setName(name);
        user.setLastLoginAt(new Date());
        user.setLoginCount(user.getLoginCount() + 1);

        // Create default preferences if necessary
        if (user.getPreferences() == null) {
            this.createDefaultPreferences(user);
        }

        ofy().save().entity(user);
        return user;
    }

    private void createDefaultPreferences(User user) {
        UserPreferences preferences = new UserPreferences();
        preferences.setNotifyOnListShared(true);
        preferences.setNotifyOnMovieAdded(true);
        preferences.setTheme(UserPreferences.THEME_DARK_BLUE);

        user.setPreferences(preferences);
    }

    private User findByEmail(String email) {
        return ofy()
                .load()
                .type(User.class)
                .filter("email", email)
                .first()
                .now();
    }
}
