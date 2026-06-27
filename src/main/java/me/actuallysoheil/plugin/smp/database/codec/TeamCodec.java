package me.actuallysoheil.plugin.smp.database.codec;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.model.team.SMPTeam;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;

@RequiredArgsConstructor
public final class TeamCodec implements Codec<SMPTeam> {

    private final @NotNull TeamOptionsCodec teamOptionsCodec;

    @Override
    public void encode(@NotNull BsonWriter writer,
                       @NotNull SMPTeam value,
                       @NotNull EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeName("_id");
        writer.writeString(value.teamId());

        writer.writeName("teamLeaderId");
        writer.writeBinaryData(new BsonBinary(value.teamLeaderId()));

        writer.writeName("teamMembers");
        writer.writeStartArray();
        value.teamMembers().stream()
                .map(BsonBinary::new)
                .forEach(writer::writeBinaryData);
        writer.writeEndArray();

        writer.writeName("teamOptions");
        this.teamOptionsCodec.encode(writer, value.teamOptions(), encoderContext);

        writer.writeEndDocument();
    }

    @Override
    public @NotNull SMPTeam decode(@NotNull BsonReader reader,
                                   @NotNull DecoderContext decoderContext) {
        reader.readStartDocument();

        val teamId = reader.readString("_id");
        val teamLeaderId = reader.readBinaryData("teamLeaderId").asUuid();

        reader.readName("teamMembers");
        reader.readStartArray();
        val teamMembers = new HashSet<UUID>();
        while (reader.readBsonType().getValue() != 0) teamMembers.add(reader.readBinaryData().asUuid());
        reader.readEndArray();

        val team = new SMPTeam(teamId, teamLeaderId);
        team.teamMembers(teamMembers);

        reader.readName("teamOptions");
        val teamOptions = this.teamOptionsCodec.decode(reader, decoderContext);
        teamOptions.smpTeam(team);
        team.teamOptions(teamOptions);

        reader.readEndDocument();

        return team;
    }

    @Override
    public @NotNull Class<SMPTeam> getEncoderClass() {
        return SMPTeam.class;
    }

}