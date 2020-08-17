import xml.etree.ElementTree as ET
import lxml.etree as LETREE
import dateutil.parser
import dateutil.tz
import datetime
import re
import os
import sys

latestSchema = False

def parse_ts(ts):
    if latestSchema:
       return parse_ts_dt(ts)
    else:
       return parse_ts_str(ts)

def parse_ts_str(ts):
   utc_dt = datetime.datetime.strptime(ts,"%Y%m%d%H%M%S")
   return (utc_dt.year, utc_dt.month, utc_dt.day, utc_dt.hour, utc_dt.minute, utc_dt.second)

def parse_ts_dt(ts):
  dt = dateutil.parser.parse(ts)
  utc_dt = dt.astimezone(dateutil.tz.tzutc())
  return (utc_dt.year, utc_dt.month, utc_dt.day, utc_dt.hour, utc_dt.minute, utc_dt.second)

def getScheduleNS():
   if latestSchema:
     return '{http://xmlns.oracle.com/falcm/flo/downtimeschedule/V2.0}'
   else:
     return '{http://xmlns.oracle.com/falcm/flo/downtimeschedule/V1.0}'

def getCrsNS():
   if latestSchema:
      return '{http://xmlns.oracle.com/falcm/flo/crs/V1.0}'
   else:
      return '{http://xmlns.oracle.com/falcm/flo/downtimeschedule/V1.0}'

def validate_schedule(schedfile, schema):
    xmlschema = LETREE.XMLSchema(file=schema)
    xmlfile = LETREE.parse(schedfile)
    if xmlschema.validate(xmlfile):
       return True
    else:
       log = xmlschema.error_log
       for error in iter(log):
          print("\terror: " + error.message)
       return False

def parse_schedule(schedfile):
    ns = getScheduleNS()
    ns_crs = getCrsNS()

    tree = ET.parse(schedfile)
    root = tree.getroot()

    sched = []
    downtime_list = root.findall(ns + 'Downtime')
    downtime_elem = downtime_list[0] # first and only <Downtime>
    sched.append((parse_ts(downtime_elem.attrib['start']),
                  parse_ts(downtime_elem.attrib['end']),
                  downtime_elem.attrib['id'],
                  downtime_elem.attrib.has_key('mode') and downtime_elem.attrib['mode'] or ""))

    crdict = {}
    cr_list = root.find(ns_crs + 'CRS').findall(ns_crs + 'CR')
    for cr_elem in cr_list:
        crdict[cr_elem.attrib['id']] = (cr_elem.attrib['type'],
                                        cr_elem.attrib.has_key('info') and cr_elem.attrib['info'] or "",
                                        cr_elem.attrib.has_key('rel') and cr_elem.attrib['rel'] or "",
                                        cr_elem.attrib.has_key('mode') and cr_elem.attrib['mode'] or "")

    poddict = {}
    podprops = {}
    pod_list = root.find(ns + 'PODS').findall(ns + 'POD')
    for pod_elem in pod_list:
        name = pod_elem.attrib['name']
        isMaster = pod_elem.attrib.has_key('isMaster') and pod_elem.attrib['isMaster'] or ""
        crs = map(lambda x: crdict[x.attrib['id']], pod_elem.findall(ns + 'CR'))
        poddict[name] = crs
        podprops.setdefault(name, {})['isMaster'] = isMaster
    
    sched.append(poddict)
    sched.append(podprops)
    return sched

def extractpods(allscheds):
    pods = set()
    for s in allscheds:
        pods.update(s[1].keys())
    return pods

def getSchema():
    if latestSchema:
       return '<path_to_v2_downtimeschedule.xsd>'
    else:
       return '<path_to_v1_schedule.xsd>'


file = sys.argv[1]
if "pod" in file:
   latestSchema = True
   
if validate_schedule(file, getSchema()): 
   parse_schedule(file)
else:
   print(file + " is invalid!")
 
