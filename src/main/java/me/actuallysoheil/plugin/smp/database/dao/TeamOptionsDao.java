package me.actuallysoheil.plugin.smp.database.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import lombok.val;
import me.actuallysoheil.plugin.smp.model.team.SMPTeamOptions;
import me.actuallysoheil.plugin.smp.manager.DatabaseManager;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

public final class TeamOptionsDao implements IDao<SMPTeamOptions, String> {

    private final @NotNull MongoCollection<Document> collection;

    public TeamOptionsDao(@NotNull DatabaseManager databaseManager) {
        this.collection = databaseManager.database()
                .getCollection("teams");
    }

    @Override
    public void insert(@NotNull SMPTeamOptions teamOptions) {
        throw new IllegalStateException("Unsupported method. Use TeamDao#insert()");
    }

    @Override
    public void update(@NotNull SMPTeamOptions teamOptions) {
        val team = teamOptions.smpTeam();
        if (team == null) return;

        this.collection.updateOne(
                Filters.eq("_id", team.teamId()),
                Updates.combine(
                        Updates.set("teamOptions.tagName", teamOptions.tagName()),
                        Updates.set("teamOptions.tagColor", teamOptions.tagColor().toString()),
                        Updates.set("teamOptions.friendlyFire", teamOptions.friendlyFire()),
                        Updates.set("teamOptions.chatMuted", teamOptions.chatMuted()),
                        Updates.set("teamOptions.homeLocation", teamOptions.homeLocation() != null ?
                                new Document()
                                        .append("world", teamOptions.homeLocation().getWorld().getName())
                                        .append("x", teamOptions.homeLocation().getX())
                                        .append("y", teamOptions.homeLocation().getY())
                                        .append("z", teamOptions.homeLocation().getZ())
                                        .append("yaw", teamOptions.homeLocation().getYaw())
                                        .append("pitch", teamOptions.homeLocation().getPitch())
                                : null)
                )
        );
    }

    @Override
    public void delete(@NonNull String s) {
        throw new IllegalStateException("Unsupported method. Use TeamDao#delete()");
    }

    @Override
    public @Nullable SMPTeamOptions findById(@NotNull String teamId) {
        throw new IllegalStateException("Unsupported method. Use TeamDao#findById()");
    }

    @Override
    public @NotNull Collection<SMPTeamOptions> findAll() {
        throw new IllegalStateException("Unsupported method. Use TeamDao#findAll()");
    }

}