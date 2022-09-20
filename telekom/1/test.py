import json


def isLeapYear(year) :
    if (year % 4 == 0 and year % 100 != 0) or year % 400 == 0:
        return True
    return False

def printGradeRequirements(data):
    gathered = data["socketPont"]["elert"] + data["mininetPont"]["elert"]
    minToPass = data["zhPont"]["max"] * data["zhPont"]["minimum"]
    
    limits = {2: 30, 3: 36, 4: 45, 5: 51}
    
    for l in limits:
        toAchieve = limits[l]
        if toAchieve < gathered + minToPass:
            needed = int(minToPass)
        elif gathered + data["zhPont"]["max"] < toAchieve:
            needed = "Remenytelen"
        else :
            needed = toAchieve - gathered
        print(l, ": ", needed)
        
def fibonacciToN(n):
    first = 1
    second = 1
    
    if n == 1:
        print(first)
    elif n == 2:
        print(second)
    elif n < 1:
        print(0)
    
    for i in range(0,n-3):
        first,second = second, first + second

    print(first + second)

def entryPoint():
    f = open("years.txt", "r")
    for line in f:
        print(isLeapYear(int(line)))
        
    f = open("grades.json", "r")
    data = json.load(f)
    printGradeRequirements(data)
    
    fibonacciToN(6)
    
entryPoint()