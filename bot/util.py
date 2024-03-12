import re


def validate_string_format(string: str) -> bool:
    pattern = r"\b(\d+(\.\d+)?([KkMmBbTt]?|\.))\b"
    return bool(re.fullmatch(pattern, string))


def convert_string_to_int(number: str) -> int:
    number = number.strip().lower().replace(",", ".")
    multiplier = 1_000 if number.endswith("k") else (1_000_000 if number.endswith("m") else (
        1_000_000_000 if number.endswith("b") else (
            1_000_000_000_000 if number.endswith("t") else 1)
        )
                                                     )
    return int(round(float(number))) if multiplier == 1 else int(round(float(number[:-1]) * multiplier))


def get_ranking_icon(level: int) -> str | None:
    if level == 0:
        return "https://minecraft.wiki/images/Netherite_Ingot_JE1_BE2.png"
    elif level == 1:
        return "https://minecraft.wiki/images/Diamond_JE3_BE3.png"
    elif level in (2, 3):
        return "https://minecraft.wiki/images/Gold_Ingot_JE4_BE2.png"
    elif level in (4, 5):
        return "https://minecraft.wiki/images/Iron_Ingot_JE3_BE2.png"
    return None
