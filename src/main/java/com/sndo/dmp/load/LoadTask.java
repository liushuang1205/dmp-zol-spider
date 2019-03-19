package com.sndo.dmp.load;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.sndo.dmp.mongo.MongoServer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LoadTask {

	private MongoCollection<Document> sourceCollection;
	private MongoCollection<Document> targetCollection;
	private FindOneAndUpdateOptions operation =  new FindOneAndUpdateOptions();
	private BlockingQueue<Document> queue = new ArrayBlockingQueue<>(200);

	public LoadTask(){
		sourceCollection = MongoServer.getCollection("try_game", "game");
		targetCollection = MongoServer.getCollection("try_game", "game_app");

		operation.upsert(true);
	}

	private class Inner implements Runnable{

		@Override
		public void run() {
			List<Document> sourceDoc = new ArrayList<>();
			try {
				while(true) {
					Document doc = queue.take();
					if (doc.isEmpty()) {
						queue.put(doc);
						break;
					} else {
						sourceDoc.add(format(doc));
						if(sourceDoc.size() % 100 == 0 && sourceDoc.size() > 0){
							insertMany(sourceDoc);
							sourceDoc.clear();
						}

					}

				}
				if(sourceDoc.size() > 0){
					insertMany(sourceDoc);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() throws InterruptedException {
		Thread[] thread = new Thread[1];
		for(int i = 0; i < thread.length; i++){
			thread[i] = new Thread(new Inner());
			thread[i].start();
		}

		query();

		for(int i = 0; i < thread.length; i++){
			thread[i].join();
		}

	}

	public void query() throws InterruptedException {
		MongoCursor<Document> docs = sourceCollection.find().iterator();
		while(docs.hasNext()){
			queue.put(docs.next());
		}
	}

	private Document format(Document doc) {
		doc.remove("_id");
		return doc;
	}

	public void insertMany(List<Document> docs){
		//for(Document doc : docs) {
			targetCollection.insertMany(docs);
		//}
	}

	public static void main(String[] args){
		LoadTask task = new LoadTask();
		try {
			task.start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		MongoServer.close();
	}

}
