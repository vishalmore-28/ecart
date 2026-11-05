package com.ecart.web;

import com.ecart.dao.WishlistDAO;
import com.ecart.model.User;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/** Helper: ids of products the current user has wishlisted (empty set for guests). */
public class Wished {
    public static Set<Integer> of(User u) throws SQLException {
        return u == null ? new HashSet<Integer>() : new WishlistDAO().ids(u.getId());
    }
}
