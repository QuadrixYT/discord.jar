package com.seailz.discordjv.model.guild;

import com.seailz.discordjv.DiscordJv;
import com.seailz.discordjv.core.Compilerable;
import com.seailz.discordjv.model.resolve.Resolvable;
import com.seailz.discordjv.model.role.Role;
import com.seailz.discordjv.model.user.User;
import com.seailz.discordjv.utils.Checker;
import com.seailz.discordjv.utils.URLS;
import com.seailz.discordjv.utils.discordapi.DiscordRequest;
import org.json.JSONObject;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a member of a guild
 *
 * @param user                       the user this guild member represents
 * @param nick                       this user's guild nickname
 * @param avatar                     the member's guild avatar hash //TODO: sort out avatars and images
 * @param roles                      The user's roles
 * @param joinedAt                   when the user joined the guild
 * @param premiumSince               when the user started boosting the guild
 * @param deaf                       whether the user is deafened in voice channels
 * @param mute                       whether the user is muted in voice channels
 * @param pending                    whether the user has not yet passed the guild's Membership Screening requirements
 * @param permissions                total permissions of the member in the channel, including overwrites, returned when in the interaction object //TODO: sort out permissions
 * @param communicationDisabledUntil when the user's timeout will expire and the user will be able to communicate in the guild again, null or a time in the past if the user is not timed out
 */
public record Member(
        User user,
        String nick,
        String avatar,
        Role[] roles,
        String joinedAt,
        String premiumSince,
        boolean deaf,
        boolean mute,
        boolean pending,
        String permissions,
        String communicationDisabledUntil,
        String guildId,
        DiscordJv discordJv
) implements Compilerable, Resolvable {

    @Override
    public JSONObject compile() {
        JSONObject obj = new JSONObject();
        obj.put("user", user.compile());
        obj.put("nick", nick);
        obj.put("avatar", avatar);
        obj.put("roles", roles);
        obj.put("joined_at", joinedAt);
        obj.put("premium_since", premiumSince);
        obj.put("deaf", deaf);
        obj.put("mute", mute);
        obj.put("pending", pending);
        obj.put("permissions", permissions);
        obj.put("communication_disabled_until", communicationDisabledUntil);
        return obj;
    }

    @NonNull
    public static Member decompile(JSONObject obj, DiscordJv discordJv, String guildId) {
        User user;
        String nick;
        String avatar;
        Role[] roles;
        String joinedAt;
        String premiumSince;
        boolean deaf;
        boolean mute;
        boolean pending;
        String permissions;
        String communicationDisabledUntil;

        try {
            user = User.decompile(obj.getJSONObject("user"), discordJv);
        } catch (Exception e) {
            user = null;
        }

        try {
            nick = obj.getString("nick");
        } catch (Exception e) {
            nick = null;
        }

        try {
            avatar = obj.getString("avatar");
        } catch (Exception e) {
            avatar = null;
        }

        try {
            List<Role> rolesList = new ArrayList<>();
            for (Object o : obj.getJSONArray("roles")) {
                rolesList.add(Role.decompile((JSONObject) o));
            }
            roles = rolesList.toArray(new Role[0]);
        } catch (Exception e) {
            roles = null;
        }

        try {
            joinedAt = obj.getString("joined_at");
        } catch (Exception e) {
            joinedAt = null;
        }

        try {
            premiumSince = obj.getString("premium_since");
        } catch (Exception e) {
            premiumSince = null;
        }

        try {
            deaf = obj.getBoolean("deaf");
        } catch (Exception e) {
            deaf = false;
        }

        try {
            mute = obj.getBoolean("mute");
        } catch (Exception e) {
            mute = false;
        }

        try {
            pending = obj.getBoolean("pending");
        } catch (Exception e) {
            pending = false;
        }

        try {
            permissions = obj.getString("permissions");
        } catch (Exception e) {
            permissions = null;
        }

        try {
            communicationDisabledUntil = obj.getString("communication_disabled_until");
        } catch (Exception e) {
            communicationDisabledUntil = null;
        }
        return new Member(user, nick, avatar, roles, joinedAt, premiumSince, deaf, mute, pending, permissions, communicationDisabledUntil, guildId, discordJv);
    }

    /**
     * 	Will throw a 403 error if the user has the ADMINISTRATOR permission or is the owner of the guild
     * @param seconds the amount of seconds to add to the timeout
     */
    public void timeout(int seconds) {
        Checker.check(seconds > 2419200, "Timeout must be less than 28 days");
        Checker.check(seconds < 0, "Timeout must be greater than 0. To remove a timeout, use removeTimeout()");

        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.SECOND, seconds);
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String timeout = dateFormat.format(date);


        new DiscordRequest(
                new JSONObject().put("communication_disabled_until", timeout),
                new HashMap<>(),
                URLS.PATCH.GUILD.MEMBER.MODIFY_GUILD_MEMBER.replace("{guild.id}", guildId).replace("{user.id}", user.id()),
                discordJv,
                URLS.PATCH.GUILD.MEMBER.MODIFY_GUILD_MEMBER,
                RequestMethod.PATCH
        ).invoke();
        System.out.println(timeout);
    }

    public void removeTimeout() {
        new DiscordRequest(
                new JSONObject().put("communication_disabled_until", JSONObject.NULL),
                new HashMap<>(),
                URLS.PATCH.GUILD.MEMBER.MODIFY_GUILD_MEMBER.replace("{guild.id}", guildId).replace("{user.id}", user.id()),
                discordJv,
                URLS.PATCH.GUILD.MEMBER.MODIFY_GUILD_MEMBER,
                RequestMethod.PATCH
        ).invoke();
    }
}