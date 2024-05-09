package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.UsernameModel;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class DCHelper {
    public boolean hasRole(@Nullable Member member, long role_id) {
        if (member == null) return false;
        return member.getRoles().stream().anyMatch(role -> role.getIdLong() == role_id);
    }

    public @NonNull String[] autoCompleteUsername(@NonNull DatabaseAdapter databaseAdapter, @NonNull String input) {
        List<UsernameModel> usernames;
        if ((usernames = databaseAdapter.getUsernames(input)) == null) return new String[0];
        return usernames
                .stream()
                .map(UsernameModel::getUsername)
                .limit(25)
                .toArray(String[]::new);
    }

    public @NonNull List<Command.Choice> autocompleteDouble(double input) {
        List<Command.Choice> choices = new ArrayList<>();
        if (input >= 1_000_000) return choices;
        choices.add(new Command.Choice(input + "K", input * 1_000));
        choices.add(new Command.Choice(input + "M", input * 1_000_000));
        choices.add(new Command.Choice(input + "B", input * 1_000_000_000));
        return choices;
    }
}
