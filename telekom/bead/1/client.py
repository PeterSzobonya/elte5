import sys
import json

class CircuitSimulator:
    def __init__(self, data):
        self.endPoints = data["end-points"]
        self.switches = data["switches"]
        self.possibleCircuits = data["possible-circuits"]
        self.linkUsages = self.__createLinkUsageDict(data["links"])
        self.simDuration = data["simulation"]["duration"]
        self.simDemands = self.__createDemandWithIds(data["simulation"]["demands"])
        self.processCounter = 1
        self.runningDemands = {}
        
    def startSimulation(self):
        # start the simulation for given duration
        for i in range(1, self.simDuration + 1):
            # check if the simulation should start or stop process at given time
            start = []
            stop = []
            for demand in self.simDemands:
                if demand["start-time"] == i:
                    start.append(demand)
                if demand["end-time"] == i:
                    stop.append(demand)
            # start/stop collection of demands the sim should try to execute
            self.__executeDemands(start, stop, i)
            
            
    def __executeDemands(self, starts, stops, time):
        # first stop the running processes to free up resources
        for stop in stops:
            self.__tryToStopProcess(stop, time)
            
        # try to start the demands
        for start in starts:
            if self.__tryToStartProcess(start):
                self.__writeResult("igény foglalás", start, time, "sikeres")
            else:
                self.__writeResult("igény foglalás", start, time, "sikertelen")
                
                
    def __writeResult(self, msg, demand, time, success):
        print(str(self.processCounter) + ". " + msg + ": " + "<->".join(demand["end-points"]) + " st:" + str(time) + " - " + success)
        self.processCounter += 1
            
    def alterLinkUsages(self, link, value):
        self.linkUsages[link]["usage"] = self.linkUsages[link]["usage"] + value
        
    def __tryToStopProcess(self, demand, time):
        # if key is not in the running demands the process was not started
        if demand["demand-id"] in self.runningDemands.keys():
            # get the circuit for the demand and generate the linkUsages keys for the circuit
            runningCircuit = self.runningDemands[demand["demand-id"]]
            toFreeUps = self.__createLinkUsagesFromCircuit(runningCircuit)
            
            # free up the linkUsages for the circuit
            for toFreeUp in toFreeUps:
                self.alterLinkUsages(toFreeUp, -1 * demand["demand"])
                
            # remove the demand from the running processes
            self.runningDemands.pop(demand["demand-id"])
            
            # write the result of stop
            self.__writeResult("igény felszabadítás", demand, time, "")
            
    def __tryToStartProcess(self, demand):
        startPoint = demand["end-points"][0]
        endPoint = demand["end-points"][1]
        
        # get possible circuits for current to start demand
        possibleCircuits = []
        for circuit in self.possibleCircuits:
            if circuit[0] == startPoint and circuit[len(circuit) - 1] == endPoint:
                possibleCircuits.append(circuit)
                
        if len(possibleCircuits) <= 0:
            return False
        
        # check if the circuit has enough capacity to start demand
        started = False
        for circuit in possibleCircuits:
            if self.__checkCircuitForCapacity(circuit, demand["demand"]):
                self.__startDemand(circuit, demand)
                started = True
                break
        
        return started
        

    def __startDemand(self, circuit, demand):
        # get the linkUsage keys for the circuit turning on
        toAlters = self.__createLinkUsagesFromCircuit(circuit)
        
        # add id and circuit to the running processes
        self.runningDemands[demand["demand-id"]] = circuit
        
        # alter linkUsages so we know the switches are used
        for toAlter in toAlters:
            self.alterLinkUsages(toAlter, demand["demand"])
        
        
    def __checkCircuitForCapacity(self, circuit, demand):
        # create array with the linkUsages keys for circuit to make it easier to check
        toChecks = self.__createLinkUsagesFromCircuit(circuit)
        
        if len(toChecks) <= 0: 
            return False
        
        # check if the linkUsage has enough capacity
        foundPath = True
        for toCheck in toChecks:
            if self.linkUsages[toCheck]["usage"] + demand > self.linkUsages[toCheck]["capacity"]:
                foundPath = False
        return foundPath
            
            
    def __createLinkUsagesFromCircuit(self, circuit):
        toChecks = []
        previous = ""
        for element in circuit:
            if previous != "":
                toChecks.append(previous + "-" + element)
            previous = element
        return toChecks
    
    def __createLinkUsageDict(self, links):
        linkUsages = {}
        for link in links:
            temp = {"points" : link["points"], "capacity": link["capacity"], "usage": 0}
            linkUsages["-".join(link["points"])] = temp
        return linkUsages
    
    def __createDemandWithIds(self, demands):
        demandsToReturn = []
        demandId = 0
        for demand in demands:
            temp = {"demand-id": demandId,"start-time": demand["start-time"], "end-time": demand["end-time"], "end-points": demand["end-points"], "demand": demand["demand"]}
            demandsToReturn.append(temp)
            demandId += 1
        return demandsToReturn


def loadJson(file):
    f = open(file)
    return json.load(f)

if len(sys.argv) > 1:
    data = loadJson(sys.argv[1])
else:
    data = loadJson("cs1.json")

# create class instance
simulator = CircuitSimulator(data)
# start the simulation
simulator.startSimulation()