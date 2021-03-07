import numpy as np
import time as T

class NN(object):

    def __init__(self, hidden_nums):
        self.layers = []
        self.layers.append(784)
        for num in range(0, hidden_nums):
            self.layers.append(50)
        self.layers.append(10)
        self.W = []
        self.b = []

    def sigmoid(self, X):
        return 1 / (1 + np.exp(-X))

    def back_sigmoid(self, X):
        a = self.sigmoid(X)
        return a * (1 - a)

    def softmax(self, X):
        a = np.exp(X)
        return a / a.sum(axis=0, keepdims=True)

    def initialize(self):
        np.random.seed(99)
        self.W.append([])
        self.b.append([])
        for k in range(1, len(self.layers)):
            self.W.append(np.random.randn(self.layers[k], self.layers[k - 1]) * 0.1)
            self.b.append(np.random.randn(self.layers[k], 1) * 0.1)

    def forward_pass(self, input):
        cache_A = []
        cache_W = []
        cache_Z = []
        cache_A.append([])
        cache_W.append([])
        cache_Z.append([])

        A = np.transpose(input)
        l = len(self.layers) - 1
        for k in range(0, l):
            Z = np.dot(self.W[k + 1], A) + self.b[k + 1]
            if k == l - 1:
                A = self.softmax(Z)
            else:
                A = self.sigmoid(Z)
            cache_A.append(A)
            cache_W.append(self.W[k + 1])
            cache_Z.append(Z)
        return A, cache_A, cache_W, cache_Z

    def backward_propagation(self, input, output, cache_A, cache_W, cache_Z):
        cache_dW = []
        cache_db = []
        n = input.shape[0]
        dA_prev = 1
        l = len(self.layers) - 1
        for k in range(l, 0, -1):
            if k == l:
                cache_A[0] = np.transpose(input)
                dZ = cache_A[k] - np.transpose(output)
            else:
                dZ = dA_prev * self.back_sigmoid(cache_Z[k])
            dW = np.dot(dZ, np.transpose(cache_A[k - 1])) / n
            db = np.sum(dZ, axis=1, keepdims=True) / n
            dA_prev = np.dot(np.transpose(cache_W[k]), dZ)
            cache_dW.insert(0, dW)
            cache_db.insert(0, db)
        cache_dW.insert(0, [])
        cache_db.insert(0, [])
        return cache_dW, cache_db

    def single_train(self, input, output, learning_rate):
        A, cache_A, cache_W, cache_Z = self.forward_pass(input)
        cache_dW, cache_db = self.backward_propagation(input, output, cache_A, cache_W, cache_Z)
        for k in range(1, len(self.layers)):
            self.W[k] -= learning_rate * cache_dW[k]
            self.b[k] -= learning_rate * cache_db[k]

    def epoch_train(self, data, label, learning_rate, batch_size):
        for k in range(0, label.shape[0], batch_size):
            input = data[k : k + batch_size, ...]
            output = label[k : k + batch_size, ...]
            self.single_train(input, output, learning_rate)

    def train(self, data, label, learning_rate, batch_size, iteration_nums):
        self.initialize()
        for k in range(0, iteration_nums):
            self.epoch_train(data, label, learning_rate, batch_size)
            # print("%d complete" % (k + 1))

    def valid(self, data, label):
        A, cache_A, cache_W, cache_Z = self.forward_pass(data)
        A = np.transpose(A)
        Y_ = np.argmax(A, axis=1)
        Y = np.argmax(label, axis=1)
        correct = 0
        for k in range(0, Y.shape[0]):
            if Y[k] == Y_[k]:
                correct += 1
        return correct * 100 / Y.shape[0]

    def test(self, data):
        A, cache_A, cache_W, cache_Z = self.forward_pass(data)
        A = np.transpose(A)
        Y_ = np.argmax(A, axis=1)
        return Y_

def data_formatter(data):
    data /= 255
    return data

def label_formatter(label):
    new_label = np.zeros([label.shape[0], 10])
    for r in range(0, label.shape[0]):
        new_label[r][int(label[r])] = 1
    return new_label

def read_files():
    train_data = np.loadtxt("train_image.csv", delimiter=",")
    train_label = np.loadtxt("train_label.csv", delimiter=",")
    test_data = np.loadtxt("test_image.csv", delimiter=",")
    # test_label = np.loadtxt("test_label.csv", delimiter=",")
    trd = data_formatter(train_data)
    trl = label_formatter(train_label)
    ted = data_formatter(test_data)
    tel = []
    return trd, trl, ted, tel

def write_files(nn, ted):
    res = nn.test(ted)
    with open("test_predictions.csv", 'w') as f:
        for re in res:
            f.write(str(re) + "\n")
    f.close()

start = T.time()
trd, trl, ted, tel = read_files()
nn = NN(2)
nn.train(trd, trl, 0.1, 40, 1000)
# print(nn.valid(ted, tel))
write_files(nn, ted)
end = T.time()
print("Time spent: %fs" % (end - start))