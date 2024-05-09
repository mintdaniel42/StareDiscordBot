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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class DCHelper {
    public boolean lacksRole(@Nullable Member member, long role_id) {
        if (member == null) return true;
        return member.getRoles().stream().noneMatch(role -> role.getIdLong() == role_id);
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

    public @NonNull List<Command.Choice> autocompleteDouble(@NonNull String input) {
        List<Command.Choice> choices = new ArrayList<>();
        if (input.isBlank()) return choices;
        Matcher matcher = Pattern.compile("[+-]?((\\d+(\\.\\d*)?)|(\\.\\d+))").matcher(input);
        double number = matcher.find() ? Double.parseDouble(matcher.group()) : 0;
        if (number >= 1_000_000) return choices;
        if (!input.endsWith("m") || !input.endsWith("b")) choices.add(new Command.Choice(number + "K", number * 1_000D));
        if (!input.endsWith("k") || !input.endsWith("b")) choices.add(new Command.Choice(number + "M", number * 1_000_000D));
        if (!input.endsWith("k") || !input.endsWith("m")) choices.add(new Command.Choice(number + "B", number * 1_000_000_000D));
        return choices;
    }
}
