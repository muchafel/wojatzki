__author__ = 'michael'


import numpy as np
import os
import cPickle as pkl
import gzip
import itertools
from collections import Counter
from nltk import FreqDist
import numpy as np
from keras.preprocessing import sequence
from keras.models import Sequential
from keras.layers import Dense, Activation, Embedding, TimeDistributed, Bidirectional, Flatten,Convolution1D, MaxPooling1D, GlobalMaxPooling1D
from keras.layers import LSTM
from keras.utils import np_utils


embeddingsPath = '/Users/michael/git/ucsm_git/stance_in_youtube/src/main/resources/list/prunedEmbeddings.84B.300d.txt'

leave_out_File = 'data/comments_watch%3Fv=QkW-0ewjiJw_reordered.txt_comments_watch%3Fv=QkW-0ewjiJw_reordered.txt'
train_folder=leave_out_File+'/train'
test_folder=leave_out_File+'/test'

files = [train_folder+'/against.txt',train_folder+'/favor.txt',train_folder+'/none.txt', test_folder+'/against.txt',test_folder+'/favor.txt',test_folder+'/none.txt']

# Mapping of the labels to integers
labelsMapping = {'none': 0, 'favor': 1, 'against': 2}

words = {}
maxSentenceLen = [0,0,0,0,0,0]
labelsDistribution = FreqDist()

distanceMapping = {'PADDING': 0, 'LowerMin': 1, 'GreaterMax': 2}
minDistance = -30
maxDistance = 30
for dis in xrange(minDistance,maxDistance+1):
    distanceMapping[dis] = len(distanceMapping)

#print distanceMapping

for fileIdx in xrange(len(files)):
    file = files[fileIdx]
    for line in open(file):
        print line
        #splits = line.strip().split('\t')

        #label = splits[0]

        #sentence = splits[3]
        tokens = line.split(" ")
        maxSentenceLen[fileIdx] = max(maxSentenceLen[fileIdx], len(tokens))
        for token in tokens:
            words[token.lower()] = True


print "Max Sentence Lengths: ",maxSentenceLen

# :: Read in word embeddings ::

word2Idx = {}
embeddings = []

for line in open(embeddingsPath):
    split = line.strip().split(" ")
    word = split[0]

    if len(word2Idx) == 0: #Add padding+unknown
        word2Idx["PADDING"] = len(word2Idx)
        vector = np.zeros(len(split)-1) #Zero vector vor 'PADDING' word
        embeddings.append(vector)

        word2Idx["UNKNOWN"] = len(word2Idx)
        vector = np.random.uniform(-0.25, 0.25, len(split)-1)
        embeddings.append(vector)

    if split[0].lower() in words:
        vector = np.array([float(num) for num in split[1:]])
        embeddings.append(vector)
        word2Idx[split[0]] = len(word2Idx)

embeddings = np.array(embeddings)

print "Embeddings shape: ", embeddings.shape
print "Len words: ", len(words)



def getWordIdx(token, word2Idx):
    """Returns from the word2Idex table the word index for a given token"""
    if token in word2Idx:
        return word2Idx[token]
    elif token.lower() in word2Idx:
        return word2Idx[token.lower()]

    return word2Idx["UNKNOWN"]


def createMatrices(files, word2Idx, maxSentenceLen=100):
    """Creates matrices for the events and sentence for the given file"""
    labels = []
    positionMatrix1 = []
    positionMatrix2 = []
    tokenMatrix = []
    noOfFiles =0
    for file in files:
        noOfFiles+=1
        noOfLines=0
        for line in open(file):
            base=os.path.basename(file)
            label= os.path.splitext(base)[0]
            labelsDistribution[label] += 1
            noOfLines+=1

            tokens = line.split(" ")

            tokenIds = np.zeros(maxSentenceLen)

            for idx in xrange(0, min(maxSentenceLen, len(tokens))):
                tokenIds[idx] = getWordIdx(tokens[idx], word2Idx)

            tokenMatrix.append(tokenIds)

            labels.append(labelsMapping[label])
        print 'lines', noOfLines,file
    print 'files', noOfFiles

    #cast to categorial
    labels = np_utils.to_categorical(labels,3)
    return labels, np.array(tokenMatrix, dtype='int32')

# :: Create token matrix ::
train_set = createMatrices(files[0:3], word2Idx, max(maxSentenceLen))
test_set = createMatrices(files[3:6], word2Idx, max(maxSentenceLen))

for label, freq in labelsDistribution.most_common(100):
    print "%s : %f%%" % (label, 100*freq / float(labelsDistribution.N()))
    print freq

########## NETWORK######

longest_sequence = max(len(s) for s in (train_set+test_set))


print 'max length', max(maxSentenceLen)



########SENNA like

# Create the train and predict_labels function
#input shape
n_in = max(maxSentenceLen)
# som eparam
n_hidden = 100
# number of labels
n_out = 3


#x = T.imatrix('x')  # the data, one word+context per row
#y = T.ivector('y')  # the labels are presented as 1D vector of [int] labels


words = Sequential()
#shape 1 = colums; shape 0 = number of train tokens , n_in =  in the windows
words.add(Embedding(output_dim=embeddings.shape[1], input_dim=embeddings.shape[0], input_length=n_in,  weights=[embeddings], trainable=False))
# Flatten = concacenates the inputes to a single vectors (numer of embedding dims * number of tokens)
words.add(Flatten())

words.add(Dense(output_dim=10, activation='tanh'))
words.add(Dense(output_dim=n_out, activation='softmax'))

words.compile(loss='categorical_crossentropy',optimizer='adam',metrics=['accuracy'])
words.summary()

for epoch in xrange(20):
    print "\n------------- Epoch %d ------------" % (epoch+1)
    #print [train_set[1]]
    #print train_set[0]
    words.fit(train_set[1], train_set[0], nb_epoch=1, batch_size=64, verbose=True, shuffle=True)
    score, acc = words.evaluate(test_set[1], test_set[0])
    print('Accuracy calculated by Keras:', acc*100)


##############




#input_length=maxSentenceLen
#model = Sequential()
#model.add(Dense(200,input_shape=(max(maxSentenceLen), 300)))
#model.add(Embedding(embeddings.shape[0],embeddings.shape[1],max(maxSentenceLen),dropout=0.2,trainable=False))

#model.add(Bidirectional(LSTM(40, return_sequences=True)))
#model.add(TimeDistributed(Dense(3)))
#model.add(Activation('softmax'))
#model.summary()

# try using different optimizers and different optimizer configs
#model.compile(loss='categorical_crossentropy',optimizer='adam',metrics=['accuracy'])

#print train_set[1]
#print train_set[0]
#print len(train_set[0])
#print len(train_set[1])


def loadPretrainedEmbedding(path, EMBEDDING_DIM, word_index):
    embeddings_index = {}
    f = list(open(path).readlines())
    for line in f:
        if line.strip().count(' ') < EMBEDDING_DIM:
            #print("Embedding WARNING (skipping entry): Entry in embedding vector had length ["+str(line.strip().count(' '))+"] but expected ["+str(EMBEDDING_DIM)+"]\n["+line+"]")
            continue
        line = line.strip()
        values = line.split()
        word = values[0]
        vector = values[1:]
        if len(vector) > EMBEDDING_DIM:
            continue
        try:
            coefs = np.asarray(vector, dtype='float32')
        except:
            continue
        embeddings_index[word] = coefs
    embedding_matrix = np.zeros((len(word_index) + 1, EMBEDDING_DIM))

    words_without_emb=0.0
    for word, i in word_index.items():
        embedding_vector = embeddings_index.get(word)
        if embedding_vector is None:
            embedding_vector = embeddings_index.get(word.lower())
        if embedding_vector is None:
            embedding_vector = np.random.rand(1, EMBEDDING_DIM)
            words_without_emb+=1
        embedding_matrix[i] = embedding_vector

    print("Loaded embedding [%s] with shape: %s embedding covers %.1f percent of words" % (path, str(embedding_matrix.shape), words_without_emb/len(word_index)*100))
    return embedding_matrix

#embedding_weights = loadPretrainedEmbedding(embeddingsPath,300,word2Idx)

#x_train=embedding_weights[train_set[1]]
#print x_train

#model.fit(train_set[1], train_set[0],nb_epoch=1)
#model.fit(x_train,train_set[0] ,nb_epoch=1)
#score, acc = model.evaluate(x_train, test_set[0])
#print('Accuracy calculated by Keras:', acc*100)
