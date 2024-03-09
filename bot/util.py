import re


def validate_string_format(s: str) -> bool:
    pattern = r"(\d+(?:[\.\d]*)?[,\d]+)([KMGTPE]?)"
    return bool(re.match(pattern, s))


def convert_string_to_int(s: str) -> int:
    s = s.strip().lower().replace(",", ".")
    multiplier = 1_000 if s.endswith("k") else (1_000_000 if s.endswith("m") else (
        1_000_000_000 if s.endswith("b") else (
            1_000_000_000_000 if s.endswith("t") else 1)
        )
    )
    result = float(s[:-1]) * multiplier
    return int(result)
