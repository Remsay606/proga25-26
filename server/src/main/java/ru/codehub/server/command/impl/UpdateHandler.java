package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Команда {@code update id}: обновляет объект.
 * USER — только свои. ADMIN — любые.
 */
public class UpdateHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    public UpdateHandler(CollectionManager manager) { this.manager = manager; }

    @Override public String getCommandName() { return "update"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role)
            throws Exception {
        String[] args = request.getArgs();
        if (args.length == 0) return CommandResponse.error("Usage: update <id>");
        long id;
        try { id = Long.parseLong(args[0]); }
        catch (NumberFormatException e) { return CommandResponse.error("Invalid id: " + args[0]); }
        MusicBand band = request.getBand();
        if (band == null) return CommandResponse.error("No MusicBand object provided");

        // ADMIN может обновить чужой объект — передаём null как ownerLogin-override
        String effectiveOwner = "ADMIN".equals(role) ? null : ownerLogin;
        CollectionManager.ModifyResult r = manager.update(id, band, ownerLogin, effectiveOwner);
        return r.success ? CommandResponse.ok(r.message) : CommandResponse.error(r.message);
    }
}
