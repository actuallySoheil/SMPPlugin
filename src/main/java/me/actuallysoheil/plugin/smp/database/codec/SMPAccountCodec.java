package me.actuallysoheil.plugin.smp.database.codec;

import lombok.val;
import me.actuallysoheil.plugin.smp.model.account.SMPAccount;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

public final class SMPAccountCodec implements Codec<SMPAccount> {

    @Override
    public void encode(@NotNull BsonWriter writer,
                       @NotNull SMPAccount value,
                       @NotNull EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeName("_id");
        writer.writeBinaryData(new BsonBinary(value.accountId()));

        writer.writeString("username", value.username());
        writer.writeBoolean("teamInvitesDisabled", value.teamInvitesDisabled());

        writer.writeEndDocument();
    }

    @Override
    public @NotNull SMPAccount decode(@NotNull BsonReader reader,
                                      @NotNull DecoderContext decoderContext) {
        reader.readStartDocument();

        val accountId = reader.readBinaryData().asUuid();
        val username = reader.readString();
        val teamInvitesDisabled = reader.readBoolean();

        reader.readEndDocument();

        val account = new SMPAccount(accountId, username);
        account.teamInvitesDisabled(teamInvitesDisabled);
        return account;
    }

    @Override
    public @NotNull Class<SMPAccount> getEncoderClass() {
        return SMPAccount.class;
    }

}