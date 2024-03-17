from config import COLOR_NORMAl, COLOR_REQUEST


class Strings:
    commands_showuser_description: str = "Eintrag anzeigen"
    commands_listusers_description: str = "Einträge auflisten"
    commands_edituser_description: str = "Eintrag bearbeiten"
    commands_edituser_request_new: str = ("Die Änderungen am Feld `{key}` für den Eintrag"
                                          "`{username}` wurden gespeichert "
                                          "und werden als Nächstes von einem Admin oder"
                                          "Moderator geprüft und (hoffentlich) freigegeben.")

    embeds_common_title: str = "Hide 'n' Seek Spielerdatenbank"
    embeds_showuser_subtitle: str = "Spielername: {username}"
    embeds_list_subtitle: str = "Spieler durchsuchen (Seite {page}/{pages})"

    embeds_showuser_rating_title: str = "Tierlistrating"
    embeds_showuser_points_title: str = "Geschätzte Punkte"
    embeds_showuser_joined_title: str = "im Modus seit"
    embeds_showuser_secondary_title: str = "Zweitaccount"
    embeds_showuser_banned_title: str = "Gebannt"
    embeds_showuser_cheating_title: str = "Cheater"

    embeds_advanced_note_title: str = "Notiz"
    embeds_advanced_top10_title: str = "Top10"
    embeds_advanced_group_title: str = "Gruppierung"

    embeds_request_title: str = "Änderungsanfrage"
    embeds_request_subtitle: str = ("Ein Nutzer möchte eine Änderung an der Datenbank vornehmen. Führe zum Freigeben "
                                    "der Änderung den unten genannten Befehl aus!")

    embeds_request_user_title: str = "Nutzer"
    embeds_request_field_title: str = "Feld"
    embeds_request_new_title: str = "Neuer Wert"
    embeds_request_old_title: str = "Alter Wert"
    embeds_request_command_title: str = "Befehl zum Freigeben"

    errors_commands_permission: str = "Du darfst diesen Befehl nicht nutzen!"
    errors_commands_nonexistent_user: str = "Der Nutzer {username} existiert nicht!"
    errors_commands_nonexistent_page: str = "Die Seite {page} gibt es nicht!"

    logging_autosave_ran: str = "Ran autosave task (again in {seconds}s)"


class Colors:
    embed_normal: int = COLOR_NORMAl
    embed_request: int = COLOR_REQUEST
