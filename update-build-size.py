import os
import re

jarsize = os.path.getsize('chip8.jar')

with open('chip8.jad','r') as f:
    content = f.read()
    jad = re.sub('MIDlet-Jar-Size: ([0-9]+)',f'MIDlet-Jar-Size: {jarsize}',content)

with open('chip8.jad','w') as f:
    f.write(jad)
    f.close()