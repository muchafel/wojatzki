__author__ = 'michael'


__author__ = 'michael'

import numpy as np
import dataLoader

from keras.models import Sequential, Model
from keras.layers import Dense, Activation, Embedding, TimeDistributed, Bidirectional, Flatten,Convolution1D, MaxPooling1D, GlobalMaxPooling1D,LSTM

from keras.layers import Activation, Dense, Dropout, Embedding, Flatten, Input, Merge, Convolution1D, MaxPooling1D

# Parameters
# ==================================================
#
# Model Variations. See Kim Yoon's Convolutional Neural Networks for
# Sentence Classification, Section 3 for detail.


# Model Hyperparameters
sequence_length = 137
embedding_dim = 300
filter_sizes = (3, 4)
num_filters = 150
dropout_prob = (0.25, 0.5)
hidden_dims = 150

# Training parameters
batch_size = 32
num_epochs = 68
val_split = 0.1

# Word2Vec parameters, see train_word2vec
min_word_count = 1  # Minimum word count
context = 10        # Context window size


embeddingsPath = '/Users/michael/git/ucsm_git/stance_in_youtube/src/main/resources/list/prunedEmbeddings.84B.300d.txt'

leave_out_File = 'data/comments_watch%3Fv=QkW-0ewjiJw_reordered.txt_comments_watch%3Fv=QkW-0ewjiJw_reordered.txt'
train_folder=leave_out_File+'/train'
test_folder=leave_out_File+'/test'

files = [train_folder+'/against.txt',train_folder+'/favor.txt',train_folder+'/none.txt', test_folder+'/against.txt',test_folder+'/favor.txt',test_folder+'/none.txt']

# Data Preparatopn
# ==================================================
#
# Load data
print("Loading data...")
x, y, vocabulary, vocabulary_inv = dataLoader.load_data(train_folder+'/favor.txt', train_folder+'/against.txt', train_folder+'/none.txt')
x_test, y_test, vocabulary_test, vocabulary_inv_test = dataLoader.load_data(test_folder+'/favor.txt', test_folder+'/against.txt', test_folder+'/none.txt')


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

embedding_weights = loadPretrainedEmbedding(embeddingsPath,300,vocabulary)

x = embedding_weights[x]


print len(x)
print len(x_test)

# Shuffle data
shuffle_indices = np.random.permutation(np.arange(len(x)))
x_shuffled = x[shuffle_indices]
y_shuffled = y[shuffle_indices]

shuffle_test_indices = np.random.permutation(np.arange(115))
x_test_shuffled = x_test[shuffle_test_indices]
y_test_shuffled = y_test[shuffle_test_indices]


print(y_shuffled)

print("Vocabulary Size: {:d}".format(len(vocabulary)))

# Building model
# ==================================================


# main sequential model
model = Sequential()
model.add(LSTM(50,batch_input_shape=(137, 300),return_sequences=True,stateful=True))
model.add(LSTM(50,
               return_sequences=False,
               stateful=True))
model.add(Dense(3))
#model.add(Dense(3, activation='sigmoid'))

# Training model
# ==================================================

print x_shuffled
print y_shuffled

model.summary()
model.compile(loss='categorical_crossentropy', optimizer='rmsprop', metrics=['accuracy'])



#model.fit(x_shuffled, y_shuffled, batch_size=batch_size,nb_epoch=num_epochs, validation_split=val_split, verbose=2)

model.fit(x_shuffled, y_shuffled ,nb_epoch=num_epochs)
score, acc = model.evaluate(x_test_shuffled, y_test_shuffled)
print('Accuracy calculated by Keras:', acc*100)