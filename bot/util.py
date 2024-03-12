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
    return int(number) if multiplier == 1 else int(round(float(number[:-1]) * multiplier))
