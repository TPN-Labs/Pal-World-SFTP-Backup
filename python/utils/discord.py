import requests


class EmbedObject:
    def __init__(self):
        self.title = None
        self.description = None
        self.color = None
        self.fields = []

    def set_title(self, title):
        self.title = title

    def set_description(self, description):
        self.description = description

    def set_color(self, color):
        self.color = color

    def add_field(self, name, value, inline):
        self.fields.append({'name': name, 'value': value, 'inline': inline})

    def to_dict(self):
        data = {k: v for k, v in vars(self).items() if v is not None}
        #if self.fields:
        #    data['fields'] = [field.to_dict() for field in self.fields]
        return data


class DiscordService:
    def __init__(self, url):
        self.url = url
        self.content = None
        self.tts = False
        self.embeds = []

    def set_content(self, content):
        self.content = content

    def set_tts(self, tts):
        self.tts = tts

    def add_embed(self, embed):
        if not isinstance(embed, EmbedObject):
            raise TypeError('embed must be an instance of EmbedObject')
        self.embeds.append(embed)

    def execute(self):
        if self.content is None and not self.embeds:
            raise ValueError("Set content or add at least one EmbedObject")

        data = {
            "content": self.content,
            "tts": self.tts,
            "embeds": [embed.to_dict() for embed in self.embeds]
        }

        response = requests.post(self.url, json=data)
        response.raise_for_status()
