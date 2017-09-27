package io.invertase.firebase.firestore;


import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

import io.invertase.firebase.Utils;

public class RNFirebaseCollectionReference {
  private static final String TAG = "RNFBCollectionReference";
  private final String appName;
  private final String path;
  private final ReadableArray filters;
  private final ReadableArray orders;
  private final ReadableMap options;
  private final Query query;

  RNFirebaseCollectionReference(String appName, String path, ReadableArray filters,
                                ReadableArray orders, ReadableMap options) {
    this.appName = appName;
    this.path = path;
    this.filters = filters;
    this.orders = orders;
    this.options = options;
    this.query = buildQuery();
  }

  void get(final Promise promise) {
    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
          Log.d(TAG, "get:onComplete:success");
          WritableMap data = FirestoreSerialize.snapshotToWritableMap(task.getResult());
          promise.resolve(data);
        } else {
          Log.e(TAG, "get:onComplete:failure", task.getException());
          RNFirebaseFirestore.promiseRejectException(promise, task.getException());
        }
      }
    });
  }

  private Query buildQuery() {
    Query query = RNFirebaseFirestore.getFirestoreForApp(appName).collection(path);
    query = applyFilters(query, filters);
    query = applyOrders(query, orders);
    query = applyOptions(query, options);

    return query;
  }

  private Query applyFilters(Query query, ReadableArray filters) {
    List<Object> filtersList = Utils.recursivelyDeconstructReadableArray(filters);

    for (Object f : filtersList) {
      Map<String, Object> filter = (Map) f;
      String fieldPath = (String) filter.get("fieldPath");
      String operator = (String) filter.get("operator");
      Object value = filter.get("value");

      switch (operator) {
        case "EQUAL":
          query = query.whereEqualTo(fieldPath, value);
          break;
        case "GREATER_THAN":
          query = query.whereGreaterThan(fieldPath, value);
          break;
        case "GREATER_THAN_OR_EQUAL":
          query = query.whereGreaterThanOrEqualTo(fieldPath, value);
          break;
        case "LESS_THAN":
          query = query.whereLessThan(fieldPath, value);
          break;
        case "LESS_THAN_OR_EQUAL":
          query = query.whereLessThanOrEqualTo(fieldPath, value);
          break;
      }
    }
    return query;
  }

  private Query applyOrders(Query query, ReadableArray orders) {
    List<Object> ordersList = Utils.recursivelyDeconstructReadableArray(orders);
    for (Object o : ordersList) {
      Map<String, Object> order = (Map) o;
      String direction = (String) order.get("direction");
      String fieldPath = (String) order.get("fieldPath");

      query = query.orderBy(fieldPath, Query.Direction.valueOf(direction));
    }
    return query;
  }

  private Query applyOptions(Query query, ReadableMap options) {
    if (options.hasKey("endAt")) {
      ReadableArray endAtArray = options.getArray("endAt");
      query = query.endAt(Utils.recursivelyDeconstructReadableArray(endAtArray));
    }
    if (options.hasKey("endBefore")) {
      ReadableArray endBeforeArray = options.getArray("endBefore");
      query = query.endBefore(Utils.recursivelyDeconstructReadableArray(endBeforeArray));
    }
    if (options.hasKey("limit")) {
      int limit = options.getInt("limit");
      query = query.limit(limit);
    }
    if (options.hasKey("offset")) {
      // Android doesn't support offset
    }
    if (options.hasKey("selectFields")) {
      // Android doesn't support selectFields
    }
    if (options.hasKey("startAfter")) {
      ReadableArray startAfterArray = options.getArray("startAfter");
      query = query.startAfter(Utils.recursivelyDeconstructReadableArray(startAfterArray));
    }
    if (options.hasKey("startAt")) {
      ReadableArray startAtArray = options.getArray("startAt");
      query = query.startAt(Utils.recursivelyDeconstructReadableArray(startAtArray));
    }
    return query;
  }
}
