package me.actuallysoheil.plugin.smp.database.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import lombok.val;
import me.actuallysoheil.plugin.smp.database.codec.TeamCodec;
import me.actuallysoheil.plugin.smp.database.codec.TeamOptionsCodec;
import me.actuallysoheil.plugin.smp.manager.DatabaseManager;
import me.actuallysoheil.plugin.smp.model.team.SMPTeam;
import org.bson.BsonBinary;
import org.bson.codecs.configuration.CodecRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public final class TeamDao implements IDao<SMPTeam, String> {

    private final @NotNull MongoCollection<SMPTeam> collection;

    public TeamDao(@NotNull DatabaseManager databaseManager) {
        val teamOptionsCodec = new TeamOptionsCodec();
        val teamCodec = new TeamCodec(teamOptionsCodec);

        this.collection = databaseManager.database()
                .getCollection("teams", SMPTeam.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        CodecRegistries.fromCodecs(teamCodec, teamOptionsCodec),
                        DatabaseManager.DEFAULT_CODEC_REGISTRIES
                ));
    }

    @Override
    public void insert(@NotNull SMPTeam team) {
        this.collection.insertOne(team);
    }

    @Override
    public void update(@NotNull SMPTeam team) {
        this.collection.updateOne(
                Filters.eq("_id", team.teamId()),
                Updates.combine(
                        Updates.set("teamLeaderId", new BsonBinary(team.teamLeaderId())),
                        Updates.set(
                                "teamMembers",
                                team.teamMembers().stream().map(BsonBinary::new).toList()
                        )
                )
        );
    }

    @Override
    public void delete(@NonNull String teamId) {
        this.collection.deleteOne(
                Filters.eq("_id", teamId)
        );
    }

    @Override
    public @Nullable SMPTeam findById(@NotNull String teamId) {
        return this.collection
                .find(Filters.eq("_id", teamId))
                .first();
    }

    @Override
    public @NotNull Collection<SMPTeam> findAll() {
        return this.collection
                .find()
                .into(new ArrayList<>());
    }

}