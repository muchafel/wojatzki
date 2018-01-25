#
# Copyright 2017
# Ubiquitous Knowledge Processing (UKP) Lab
# Technische Universität Darmstadt
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, see http://www.gnu.org/licenses/.


from __future__ import print_function

from sys import argv
import numpy as np
import argparse


def numpyizeDataVector(vec):
	trainVecNump=[]
	file = open(vec, 'r')
	for l in file.readlines():
		l = l.strip()
		trainVecNump.append(np.fromstring(l, dtype=int, sep=' '))
	file.close()
	return trainVecNump

def numpyizeOutcomeVector(vec):
	file = open(vec, 'r')
	v=""
	for l in file.readlines():
		l = l.strip()
		v=np.fromstring(l, dtype=float, sep=' ')
	file.close()
	return v

def loadEmbeddings(emb):
	f = open(emb, 'r')
	embData = f.readlines()
	f.close()
	dim = len(embData[0].split())-1
	matrix = np.zeros((len(embData)+1, dim))
	for e in embData:
		e = e.strip()
		if not e:
			continue
		idx = e.find(" ")
		id = e[:idx]
		vector = e[idx+1:]
		matrix[int(id)]=np.asarray(vector.split(" "), dtype='float32')
	return matrix, dim
	
def mapData(input, map):
	out = []
	for s in input:
		out_s=[]
		for w in s:
			out_s.append(map[w])
		out.append(out_s)
	return out


def readInstances(vec):
	dic={}
	startIdx=0
	trainVec1=[]
	trainVec2=[]
	file = open(vec, 'r')
	for l in file.readlines():
		trainVecPart1=[]
		trainVecPart2=[]
		l = l.strip()
		first=True
		for part in l.split(" "):
			if(part=="$"):
				first=False;
				#skip $
				continue
			if part not in dic:
				dic[part]=startIdx
				startIdx+=1
			
			if(first):
				trainVecPart1.append(part)
			else:
				trainVecPart2.append(part)
				
		trainVec1.append(trainVecPart1)
		trainVec2.append(trainVecPart2)
	file.close()
	return trainVec1, trainVec2, dic, startIdx
	
	
def readInstancesTest2(vec,dic,startIdx):
	trainVec1=[]
	trainVec2=[]
	file = open(vec, 'r')
	for l in file.readlines():
		trainVecPart1=[]
		trainVecPart2=[]
		l = l.strip()
		first=True
		for part in l.split(" "):
			if(part=="$"):
				first=False;
				#skip $
				continue
			if part not in dic:
				dic[part]=startIdx
				startIdx+=1
			
			if(first):
				trainVecPart1.append(part)
			else:
				trainVecPart2.append(part)
				
		trainVec1.append(trainVecPart1)
		trainVec2.append(trainVecPart2)
	file.close()
	return trainVec1, trainVec2, dic
	
def readInstancesTest(vec,dic,startIdx):
	trainVec=[]
	file = open(vec, 'r')
	for l in file.readlines():
		trainVecPart=[]
		l = l.strip()
		for part in l.split(" "):
			trainVecPart.append(part)
			if part not in dic:
				dic[part]=startIdx
				startIdx+=1
				
		trainVec.append(trainVecPart)
	file.close()
	return trainVec, dic
	
def getEmbeddingsIndex(embedding):
	embeddings_index = {}
	f = open(embedding, 'r')
	embData = f.readlines()
	f.close()
	dim = len(embData[0].split())-1
	for line in embData:
			values = line.split()
			word = values[0]
			coefs = np.asarray(values[1:], dtype='float32')
			embeddings_index[word] = coefs
	f.close()

	print('Found %s word vectors.' % len(embeddings_index))
	return embeddings_index, dim


def loadPretrainedEmbedding(path, word_index):
	embeddings_index = {}
	f = open(embedding, 'r')
	embData = f.readlines()
	EMBEDDING_DIM = len(embData[0].split())-1
	f.close()
	f = open(path,encoding='utf-8',errors='ignore')
	for line in embData:
		if line.count(' ') < EMBEDDING_DIM:
		# probably first line
			continue
		line = line.strip()
		values = line.split()
		word = values[0]
		try:
			coefs = np.asarray(values[1:], dtype='float32')
		except:
			continue
		embeddings_index[word] = coefs
	f.close()
	embedding_matrix = np.zeros((len(word_index) + 1, EMBEDDING_DIM))
	for word, i in word_index.items():
		embedding_vector = embeddings_index.get(word)
		if embedding_vector is None:
			embedding_vector = embeddings_index.get(word.lower())
		if embedding_vector is not None:
			# words not found in embedding index will be all-zeros.
			try:
				embedding_matrix[i] = embedding_vector
			except:
				print("assigning vector for word [" + str(word)+ "] failed Len: %d" % len(embedding_vector))
	return embedding_matrix

def getMatrix(embeddings_index,train,dim):
	matrix={}
	j=0;
	for sentence in train:
		embedding_matrix = np.zeros((len(embeddings_index) + 1, dim))
		i=0;
		for word in embeddings_index:
			embedding_vector = embeddings_index.get(word)
			if embedding_vector is not None:
			# words not found in embedding index will be all-zeros.
				embedding_matrix[i] = embedding_vector
				i+=1
		#matrix.[j]=(embedding_matrix)
	return matrix

def runExperiment(seed, trainVec, trainOutcome, testVec, testOutcome, embedding, maximumLength, predictionOut):

	np.random.seed(seed)
	from keras.preprocessing import sequence
	from keras.models import Sequential
	from keras.layers import Dense, Dropout, Activation
	from keras.layers import Embedding, TimeDistributed, Bidirectional, Flatten,Convolution1D, MaxPooling1D, GlobalMaxPooling1D, Merge
	from keras.layers import Embedding
	from keras.layers import LSTM
	from keras.layers import Conv1D, MaxPooling1D


	train1, train2, dic, startIdx= readInstances(trainVec)	
	#print(train1)
	#print(train2)
	test1,test2, dic=readInstancesTest2(testVec,dic,startIdx);
	
	embeddingsIndex = loadPretrainedEmbedding(embedding,dic)
	#embeddingsTrainMatrix1=getMatrix(embeddingsIndex,train1,dim)
	#embeddingsTrainMatrix2=getMatrix(embeddingsIndex,train2,dim)
	#embeddingsTestMatrix=getMatrix(embeddingsIndex,test,dim)
	#print(len(embeddingsTrainMatrix1))
	#print(len(embeddingsTestMatrix))
	
	train1=mapData(train1,dic)
	train2=mapData(train2,dic)
	test1=mapData(test1,dic)
	test2=mapData(test2,dic)
	
	trainOutcome = numpyizeOutcomeVector(trainOutcome)
	testOutcome = numpyizeOutcomeVector(testOutcome)
	print(trainOutcome)
	print(testOutcome)

	x_train1 = sequence.pad_sequences(train1, maxlen=int(maximumLength))
	x_train2 = sequence.pad_sequences(train1, maxlen=int(maximumLength))
	x_test1 = sequence.pad_sequences(test1, maxlen=int(maximumLength))
	x_test2 = sequence.pad_sequences(test2, maxlen=int(maximumLength))

	print(len(x_train1))
	print(len(train2))

	y_train = trainOutcome
	y_test = testOutcome

	
	print(len(y_test))

	left = Sequential()
	left.add(Embedding(output_dim=embeddingsIndex.shape[1], input_dim=embeddingsIndex.shape[0], input_length=x_train1.shape[1],  weights=[embeddingsIndex], trainable=False))
	left.add(Dropout(0.5))
	left.add(Conv1D(200,2,padding='valid',activation='relu',strides=1))
	left.add(GlobalMaxPooling1D())
	left.add(Dense(50, kernel_initializer='normal', activation='relu'))
	left.add(Dense(10, kernel_initializer='normal', activation='relu'))
	#model.add(Dense(1, kernel_initializer='normal'))
	
	right = Sequential()
	right.add(Embedding(output_dim=embeddingsIndex.shape[1], input_dim=embeddingsIndex.shape[0], input_length=x_train2.shape[1],  weights=[embeddingsIndex], trainable=False))
	right.add(Dropout(0.5))
	right.add(Conv1D(200,2,padding='valid',activation='relu',strides=1))
	right.add(GlobalMaxPooling1D())
	right.add(Dense(50, kernel_initializer='normal', activation='relu'))
	right.add(Dense(10, kernel_initializer='normal', activation='relu'))
	

	model = Sequential()
	model.add(Merge([left, right], mode='concat'))
	model.add(Dense(1, kernel_initializer='normal'))	
	
	model.compile(loss='mean_squared_error', optimizer='adam')
	#model.fit(x_train1, y_train, epochs=10, shuffle=True)
	model.fit([x_train1, x_train2], y_train, epochs=10, shuffle=True)
	
	
	#prediction = model.predict(x_test)
	prediction = model.predict([x_test1, x_test2])
	

	predictionFile = open(predictionOut, 'w')
	predictionFile.write("#Gold\tPrediction\n")
	for i in range(0, len(prediction)):
		#print(prediction[i][0])
		predictionFile.write(str(y_test[i]) +"\t" + str(prediction[i][0])+ "\n")
	predictionFile.close()


if  __name__ =='__main__':
	parser = argparse.ArgumentParser(description="")
	parser.add_argument("--trainData", nargs=1, required=True)
	parser.add_argument("--trainOutcome", nargs=1, required=True)
	parser.add_argument("--testData", nargs=1, required=True)
	parser.add_argument("--testOutcome", nargs=1, required=True)	
	parser.add_argument("--embedding", nargs=1, required=True)	
	parser.add_argument("--maxLen", nargs=1, required=True)
	parser.add_argument("--predictionOut", nargs=1, required=True)
	parser.add_argument("--seed", nargs=1, required=False)	
	
	
	args = parser.parse_args()
	
	trainData = args.trainData[0]
	trainOutcome = args.trainOutcome[0]
	testData = args.testData[0]
	testOutcome = args.testOutcome[0]
	embedding = args.embedding[0]
	maxLen = args.maxLen[0]
	predictionOut = args.predictionOut[0]
	if not args.seed:
		seed=897534793	#random seed
	else:
		seed = args.seed[0]
	
	runExperiment(int(seed), trainData, trainOutcome, testData, testOutcome, embedding, int(maxLen), predictionOut)	