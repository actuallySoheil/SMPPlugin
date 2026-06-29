package me.actuallysoheil.plugin.smp.database.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import me.actuallysoheil.plugin.smp.database.codec.SMPAccountCodec;
import me.actuallysoheil.plugin.smp.manager.DatabaseManager;
import me.actuallysoheil.plugin.smp.model.account.SMPAccount;
import org.bson.BsonBinary;
import org.bson.codecs.configuration.CodecRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class AccountDao implements IDao<SMPAccount, UUID> {

    private final @NotNull MongoCollection<SMPAccount> collection;

    public AccountDao(@NotNull DatabaseManager databaseManager) {
        this.collection = databaseManager.database()
                .getCollection("accounts", SMPAccount.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        CodecRegistries.fromCodecs(new SMPAccountCodec()),
                        DatabaseManager.DEFAULT_CODEC_REGISTRIES
                ));
    }

    @Override
    public void insert(@NotNull SMPAccount account) {
        this.collection.insertOne(account);
    }

    @Override
    public void update(@NotNull SMPAccount account) {
        this.collection.updateOne(
                Filters.eq("_id", new BsonBinary(account.accountId())),
                Updates.combine(
                        Updates.set("username", account.username()),
                        Updates.set("teamInvitesDisabled", account.teamInvitesDisabled())
                )
        );
    }

    @Override
    public void delete(@NonNull UUID accountId) {
        this.collection.deleteOne(
                Filters.eq("_id", new BsonBinary(accountId))
        );
    }

    @Override
    public @Nullable SMPAccount findById(@NotNull UUID accountId) {
        return this.collection
                .find(Filters.eq("_id", new BsonBinary(accountId)))
                .first();
    }

    @Override
    public @NotNull Collection<SMPAccount> findAll() {
        return this.collection.find().into(new ArrayList<>());
    }

}