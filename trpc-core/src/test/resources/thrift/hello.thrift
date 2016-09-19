namespace java com.github.sofn.trpc.demo

include "fb303.thrift"

service Hello extends fb303.FacebookService{
  string hi(1:string name);
}