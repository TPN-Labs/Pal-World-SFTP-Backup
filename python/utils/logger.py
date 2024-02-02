import logging
import sys

handler = logging.StreamHandler(sys.stdout)
palo_logger = logging.getLogger()
palo_logger.addHandler(handler)
palo_logger.setLevel("INFO")
