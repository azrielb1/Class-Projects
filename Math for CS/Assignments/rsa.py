import random
from math import gcd

def generate_keypair():
    p, q = random.sample([53, 59, 61, 67, 71, 73, 79, 83, 89, 97], 2)
    n = p * q
    totient = (p-1) * (q-1)
    e = random.choice([e for e in range(totient) if gcd(e, totient) == 1])
    d = random.choice([d for d in range(totient) if (d*e)%totient == 1])
    
    privateKeyTXT = open("privateKey.txt", "w")
    privateKeyTXT.write(str(n)+','+str(e)+"\n")
    privateKeyTXT.close()

    publicKeyTXT = open("publicKey.txt", "w")
    publicKeyTXT.write(str(n)+','+str(d)+"\n")
    publicKeyTXT.close()
    
def rsa_encrypt(m):
    publicKey = open("publicKey.txt", "r")
    n, e = publicKey.read().split(',')
    publicKey.close()
    return pow(m, int(e), int(n))

def rsa_decrypt(c):
    privateKey = open("privateKey.txt", "r")
    n, d = privateKey.read().split(',')
    privateKey.close()
    return pow(c, int(d), int(n))
