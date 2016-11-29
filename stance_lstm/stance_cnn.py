__author__ = 'michael'

import numpy as np
import dataLoader

from keras.models import Sequential, Model
from keras.layers import Activation, Dense, Dropout, Embedding, Flatten, Input, Merge, Convolution1D, MaxPooling1D
from keras.layers import Dense, Activation, Embedding, TimeDistributed, Bidirectional, Flatten,Convolution1D, MaxPooling1D, GlobalMaxPooling1D,LSTM


# Parameters
# ==================================================
#
# Model Variations. See Kim Yoon's Convolutional Neural Networks for
# Sentence Classification, Section 3 for detail.

model_variation = 'CNN-rand'  #  CNN-rand | CNN-non-static | CNN-static
print('Model variation is %s' % model_variation)

# Model Hyperparameters
sequence_length = 137
embedding_dim = 300
filter_sizes = (3, 4)
num_filters = 150
dropout_prob = (0.25, 0.5)
hidden_dims = 150

# Training parameters
batch_size = 32
num_epochs = 5
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

print(x)
x = embedding_weights[x]
print(x)
x_test = embedding_weights[x_test]


print len(x)
#print len(x_test)

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
#
# graph subnet with one input and one output,
# convolutional layers concateneted in parallel
graph_in = Input(shape=(sequence_length, embedding_dim))
convs = []
for fsz in filter_sizes:
    conv = Convolution1D(nb_filter=num_filters,
                         filter_length=fsz,
                         border_mode='valid',
                         activation='relu',
                         subsample_length=1)(graph_in)
    pool = MaxPooling1D(pool_length=2)(conv)
    flatten = Flatten()(pool)
    convs.append(flatten)

if len(filter_sizes)>1:
    out = Merge(mode='concat')(convs)
else:
    out = convs[0]

graph = Model(input=graph_in, output=out)

# main sequential model
model = Sequential()

model.add(Dropout(dropout_prob[0], input_shape=(sequence_length, embedding_dim)))
model.add(graph)
model.add(Bidirectional(LSTM(200, return_sequences=True)))
model.add(Dense(hidden_dims))
model.add(Dropout(dropout_prob[1]))
model.add(Activation('relu'))
model.add(Dense(3))
model.add(Activation('sigmoid'))
model.compile(loss='categorical_crossentropy', optimizer='rmsprop', metrics=['accuracy'])

# Training model
# ==================================================

print x_shuffled
print y_shuffled

model.summary()


#model.fit(x_shuffled, y_shuffled, batch_size=batch_size,nb_epoch=num_epochs, validation_split=val_split, verbose=2)

model.fit(x_shuffled, y_shuffled ,nb_epoch=num_epochs)
score, acc = model.evaluate(x_test_shuffled, y_test_shuffled)
print('Accuracy calculated by Keras:', acc*100, score)