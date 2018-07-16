package com.richodemus.bug;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class JavaBug {

  // For some reason this doesn't work
  private CompletableFuture<A> methodThatDoesntWork(CompletionStage<String> stage) {
    return stage.thenApply(result -> {
      if (false) {
        return new B();
      }
      return new B();
    }).toCompletableFuture();
  }

  // If we cast explicitly then it works
  private CompletableFuture<A> explicitCast(CompletionStage<String> stage) {
    return stage.thenApply(result -> {
      if (false) {
        return (A) new B();
      }
      return new B(); // Why is it enough to only cast the previous return?
    }).toCompletableFuture();
  }

  // Below are two ways of solving it by giving the compiler some help

  // If we cast the lambda to explicitly specify that it's A then it works
  private CompletableFuture<A> castLambda(CompletionStage<String> stage) {
    return stage.thenApply((Function<String, A>) result -> {
      if (false) {
        return new B();
      }
      return new B();
    }).toCompletableFuture();
  }

  // Extracting a method makes the compiler happy
  private CompletableFuture<A> avoidingCast(CompletionStage<String> stage) {
    return stage.thenApply(apply())
        .toCompletableFuture();
  }

  private Function<String, A> apply() {
    return result -> {
      if (false) {
        return new B();
      }
      return new B();
    };
  }
}
