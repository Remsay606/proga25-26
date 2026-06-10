package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.command.ServerCommandHandler;

/** Команда {@code help}: справка с учётом роли пользователя. */
public class HelpHandler implements ServerCommandHandler {

    @Override public String getCommandName() { return "help"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role) {
        StringBuilder sb = new StringBuilder();
        sb.append("Available commands:\n");
        sb.append("  help                         - show this help\n");
        sb.append("  info                         - collection info\n");
        sb.append("  show                         - show all elements (sorted by size)\n");
        sb.append("  add                          - add a new element (you become owner)\n");
        sb.append("  update <id>                  - update element by id");
        if ("ADMIN".equals(role)) sb.append(" [any owner]");
        else sb.append(" [yours only]");
        sb.append("\n");
        sb.append("  remove_by_id <id>            - remove element by id");
        if ("ADMIN".equals(role)) sb.append(" [any owner]");
        else sb.append(" [yours only]");
        sb.append("\n");
        sb.append("  clear                        - remove elements");
        if ("ADMIN".equals(role)) sb.append(" [ALL objects]\n");
        else sb.append(" [yours only]\n");
        sb.append("  remove_head                  - remove first element");
        if ("ADMIN".equals(role)) sb.append(" [any owner]\n");
        else sb.append(" [yours only]\n");
        sb.append("  add_if_min                   - add if size < minimum\n");
        sb.append("  min_by_creation_date         - oldest element\n");
        sb.append("  group_counting_by_name       - group by name\n");
        sb.append("  filter_contains_name <sub>   - filter by name substring\n");
        sb.append("  exit                         - log out\n");

        if ("ADMIN".equals(role)) {
            sb.append("\n--- ADMIN commands ---\n");
            sb.append("  promote_user <login>         - promote user to ADMIN\n");
            sb.append("  demote_admin <login>         - demote admin to USER\n");
        }

        sb.append("\nYour role: ").append(role);
        return CommandResponse.ok(sb.toString());
    }
}
